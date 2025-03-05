package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"mime/multipart"
	"net/http"
	"os"
	"strconv"
	"sync"
	"time"
)

// Define ANSI color codes
const (
	Red     = "\033[31m"
	Green   = "\033[32m"
	Yellow  = "\033[33m"
	Blue    = "\033[34m"
	Magenta = "\033[35m"
	Cyan    = "\033[36m"
	Reset   = "\033[0m"
)

// Define default values
var (
	count     = 1
	parallel  = true
	workDir   = "."
	username  = "file-server@user.com"
	password  = "123456"
	largeFile = "large-63mb.pdf"
)

// Counter struct to store total time, success, and error counts
type Counter struct {
	TotalTime int64
	Success   int
	Error     int
	mu        sync.Mutex
}

// Function to update counters safely
func (c *Counter) Update(totalTime int64, success, error int) {
	c.mu.Lock()
	defer c.mu.Unlock()
	c.TotalTime += totalTime
	c.Success += success
	c.Error += error
}

// Function to make a request and measure time
func request(port int, i int, counter *Counter, wg *sync.WaitGroup) {
	defer wg.Done()

	fmt.Printf("%sRequest %d/%d to port %d:%s\n", Cyan, i, count, port, Reset)

	// Measure the time taken for the authorization request
	authStartTime := time.Now()
	authURL := fmt.Sprintf("http://localhost:%d/api/file-server/v1/authorization", port)
	authPayload := map[string]string{
		"username": username,
		"password": password,
	}
	authJSON, _ := json.Marshal(authPayload)
	authResp, err := http.Post(authURL, "application/json", bytes.NewBuffer(authJSON))
	if err != nil {
		fmt.Printf("%sFailed to get JWT token for port %d: %v%s\n", Red, port, err, Reset)
		counter.Update(0, 0, 1)
		return
	}
	defer authResp.Body.Close()

	// Validate authorization response status code
	if authResp.StatusCode != http.StatusOK {
		fmt.Printf("%sAuthorization failed for port %d: Status Code %d%s\n", Red, port, authResp.StatusCode, Reset)
		counter.Update(0, 0, 1)
		return
	}

	var authResult map[string]interface{}
	json.NewDecoder(authResp.Body).Decode(&authResult)
	jwtToken, ok := authResult["jwt"].(string)
	if !ok || jwtToken == "" {
		fmt.Printf("%sFailed to get JWT token for port %d%s\n", Red, port, Reset)
		counter.Update(0, 0, 1)
		return
	}

	authTime := time.Since(authStartTime).Milliseconds()

	// Measure the time taken for the file upload request
	uploadStartTime := time.Now()
	uploadURL := fmt.Sprintf("http://localhost:%d/api/file-server/v1/files/upload", port)
	file, err := os.Open(fmt.Sprintf("%s/%s", workDir, largeFile))
	if err != nil {
		fmt.Printf("%sFailed to open file for port %d: %v%s\n", Red, port, err, Reset)
		counter.Update(0, 0, 1)
		return
	}
	defer file.Close()

	var requestBody bytes.Buffer
	writer := multipart.NewWriter(&requestBody)
	part, _ := writer.CreateFormFile("resource", largeFile)
	io.Copy(part, file)
	writer.WriteField("fileName", "large-file")
	writer.WriteField("dirName", "overload-test")
	writer.Close()

	req, _ := http.NewRequest("POST", uploadURL, &requestBody)
	req.Header.Set("Authorization", jwtToken)
	req.Header.Set("Content-Type", writer.FormDataContentType())

	client := &http.Client{}
	uploadResp, err := client.Do(req)
	if err != nil {
		fmt.Printf("%sUpload failed for port %d: %v%s\n", Red, port, err, Reset)
		counter.Update(0, 0, 1)
		return
	}
	defer uploadResp.Body.Close()

	// Validate upload response status code
	if uploadResp.StatusCode != http.StatusOK {
		fmt.Printf("%sUpload failed for port %d: Status Code %d%s\n", Red, port, uploadResp.StatusCode, Reset)
		counter.Update(0, 0, 1)
		return
	}

	uploadTime := time.Since(uploadStartTime).Milliseconds()
	totalTime := authTime + uploadTime

	// Check if the upload request was successful by looking for "fileDownloadUri" in the response
	var uploadResult map[string]interface{}
	json.NewDecoder(uploadResp.Body).Decode(&uploadResult)
	if _, ok := uploadResult["fileDownloadUri"]; ok {
		fmt.Printf("%sUpload successful for port %d%s\n", Green, port, Reset)
		counter.Update(totalTime, 1, 0)
	} else {
		fmt.Printf("%sUpload failed for port %d%s\n", Red, port, Reset)
		counter.Update(totalTime, 0, 1)
	}

	fmt.Printf("%sAuthorization time for port %d: %dms%s\n", Blue, port, authTime, Reset)
	fmt.Printf("%sUpload time for port %d: %dms%s\n", Blue, port, uploadTime, Reset)
	fmt.Printf("%sTotal time for port %d: %dms%s\n\n", Magenta, port, totalTime, Reset)
}

func main() {
	// Parse arguments
	if len(os.Args) > 1 {
		for i := 1; i < len(os.Args); i++ {
			if os.Args[i] == "--count" && i+1 < len(os.Args) {
				count, _ = strconv.Atoi(os.Args[i+1])
				i++
			} else if os.Args[i] == "--sync" {
				parallel = false
			} else if os.Args[i] == "--async" {
				parallel = true
			}
		}
	}

	// Counters for Java and Go
	javaCounter := &Counter{}
	goCounter := &Counter{}

	// WaitGroup to wait for all goroutines to complete
	var wg sync.WaitGroup

	// Make requests in parallel to both ports
	for i := 1; i <= count; i++ {
		fmt.Printf("%sBenchmarking request %d/%d:%s\n", Cyan, i, count, Reset)
		wg.Add(2)
		go request(8080, i, javaCounter, &wg) // Java Spring Boot
		go request(8081, i, goCounter, &wg)   // Go
		if !parallel {
			wg.Wait()
		}
	}

	// Wait for all goroutines to complete
	wg.Wait()

	// Calculate average time for each server
	avgTimeJava := float64(javaCounter.TotalTime) / float64(count)
	avgTimeGo := float64(goCounter.TotalTime) / float64(count)

	// Log benchmark results
	fmt.Printf("%sBenchmark Results:%s\n", Cyan, Reset)
	fmt.Printf("%sJava Spring Boot (port 8080):%s\n", Yellow, Reset)
	fmt.Printf("  %sTotal time for %d requests: %dms%s\n", Cyan, count, javaCounter.TotalTime, Reset)
	fmt.Printf("  %sAverage time per request: %.2fms%s\n", Cyan, avgTimeJava, Reset)
	fmt.Printf("  %sSuccessful requests: %d%s\n", Green, javaCounter.Success, Reset)
	fmt.Printf("  %sFailed requests: %d%s\n", Red, javaCounter.Error, Reset)
	fmt.Printf("%sGo (port 8081):%s\n", Yellow, Reset)
	fmt.Printf("  %sTotal time for %d requests: %dms%s\n", Cyan, count, goCounter.TotalTime, Reset)
	fmt.Printf("  %sAverage time per request: %.2fms%s\n", Cyan, avgTimeGo, Reset)
	fmt.Printf("  %sSuccessful requests: %d%s\n", Green, goCounter.Success, Reset)
	fmt.Printf("  %sFailed requests: %d%s\n", Red, goCounter.Error, Reset)

	fmt.Printf("%sBenchmarking completed.%s\n", Green, Reset)
}

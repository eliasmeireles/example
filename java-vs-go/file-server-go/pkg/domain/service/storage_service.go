package service

import (
	"encoding/json"
	"file-server-go/gen"
	"file-server-go/pkg/env"
	"file-server-go/pkg/utils"
	"fmt"
	"github.com/labstack/gommon/log"
	"io"
	"mime/multipart"
	"os"
	"strings"
	"sync"
)

type StorageService interface {
	SaveFile(
		file multipart.File,
		fileHeader *multipart.FileHeader,
		dirName string,
		fileName string,
	) (map[string]interface{}, error)
	DownloadFile(filePath string) (multipart.File, error)
	List(dirPath string) (gen.Data, error)
}

type _storageServiceImpl struct {
	appEnv env.AppEnv
}

var (
	storageServiceOnce     sync.Once
	storageServiceInstance StorageService
)

func GetStorageService() StorageService {
	storageServiceOnce.Do(func() {
		storageServiceInstance = &_storageServiceImpl{
			appEnv: *env.GetAppEnv(),
		}
	})
	return storageServiceInstance
}

func (s _storageServiceImpl) SaveFile(
	file multipart.File,
	fileHeader *multipart.FileHeader,
	dirName string,
	fileName string,
) (map[string]interface{}, error) {
	// Create the directory if it doesn't exist
	if err := os.MkdirAll(dirName, os.ModePerm); err != nil {
		return nil, err
	}

	fileExtension := utils.GetFileExtension(fileHeader)

	filePath, err := utils.NormalizeFileName(s.appEnv.StoragePath, dirName, fileName, fileExtension)

	if err != nil {
		return nil, err
	}

	outFile, err := os.Create(filePath)

	if err != nil {
		return nil, err
	}

	defer func(outFile *os.File) {
		err := outFile.Close()
		if err != nil {
			log.Errorf("Failed to close file: %v", err)
		}
	}(outFile)

	// Copy the uploaded file to the server file
	_, err = io.Copy(outFile, file)
	if err != nil {
		return nil, err
	}

	fullFileName := fmt.Sprintf("%s/%s%s", dirName, fileName, fileExtension)

	// Generate file metadata for the response
	fileInfo := map[string]interface{}{
		"fileName":        fileName,
		"fileDownloadUri": fmt.Sprintf("/files/download?filePath=%s", fullFileName),
		"contentType":     fileExtension,
		"size":            utils.GetFileSize(fileHeader),
		"extension":       fileExtension,
	}

	return fileInfo, nil
}

func (s _storageServiceImpl) DownloadFile(filePath string) (multipart.File, error) {
	// Open the file
	fixPath := strings.TrimPrefix(filePath, "/")
	path := s.appEnv.StoragePath + "/" + fixPath
	file, err := os.Open(path)

	if err != nil {
		return nil, fmt.Errorf("failed to open file: %v", err)
	}

	// Return the file as a multipart.File
	return file, nil
}

func (s _storageServiceImpl) List(dirPath string) (gen.Data, error) {
	// Read the directory
	fixPath := strings.TrimPrefix(dirPath, "/")
	fixPath = strings.TrimSuffix(fixPath, "./")

	path := s.appEnv.StoragePath + "/" + fixPath

	// Prepare a slice to hold the file paths
	var paths []string

	// Recursively list files and directories
	err := utils.ListFilesRecursively(path, &paths, func(filePath string) string {
		cleanPath := strings.TrimPrefix(filePath, s.appEnv.StoragePath)
		return "/files/download?filePath=" + cleanPath
	})

	if err != nil {
		return gen.Data{}, fmt.Errorf("failed to list files recursively: %v", err)
	}

	// Convert the slice to a JSON-compatible interface{}
	var pathInterface interface{}
	pathsJSON, err := json.Marshal(paths)
	if err != nil {
		return gen.Data{}, fmt.Errorf("failed to marshal paths to JSON: %v", err)
	}
	if err := json.Unmarshal(pathsJSON, &pathInterface); err != nil {
		return gen.Data{}, fmt.Errorf("failed to unmarshal paths to interface{}: %v", err)
	}

	if pathInterface == nil {
		pathInterface = []string{}
	}

	// Return the result as gen.Data
	return gen.Data{
		Paths: &pathInterface,
	}, nil
}

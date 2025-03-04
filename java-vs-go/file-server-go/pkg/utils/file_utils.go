package utils

import (
	"fmt"
	"mime/multipart"
	"os"
	"path/filepath"
	"regexp"
	"strings"
)

// GetFileExtension returns the file extension from a multipart.FileHeader.
// Example: "example.txt" -> "txt"
func GetFileExtension(fileHeader *multipart.FileHeader) string {
	// Get the filename from the file header
	filename := fileHeader.Filename

	// Use filepath.Ext to get the extension (including the dot)
	ext := filepath.Ext(filename)

	// Remove the leading dot and convert to lowercase
	if ext != "" {
		return "." + strings.ToLower(ext[1:])
	}

	// Return an empty string if no extension is found
	return ""
}

// GetFileSize returns the size of the file in bytes.
func GetFileSize(fileHeader *multipart.FileHeader) int64 {
	return fileHeader.Size
}

// NormalizeFileName normalizes the file name by removing slashes and whitespace,
// appending the file extension, and combining it with the directory name.
// It also ensures the full directory path exists.
func NormalizeFileName(basePath string, dirName string, fileName string, fileExtension string) (string, error) {
	// Remove slashes from the file name
	normalizedFileName := strings.ReplaceAll(fileName, "/", "")

	// Remove whitespace from the file name
	re := regexp.MustCompile(`\s`)
	normalizedFileName = re.ReplaceAllString(normalizedFileName, "")

	// Append the file extension if not already present
	if !strings.HasSuffix(normalizedFileName, fileExtension) {
		normalizedFileName += fileExtension
	}

	// Combine the base path, directory name, and normalized file name
	fullPath := filepath.Join(basePath, dirName, normalizedFileName)

	// Ensure the full directory path exists
	dirPath := filepath.Dir(fullPath)
	if err := os.MkdirAll(dirPath, os.ModePerm); err != nil {
		return "", fmt.Errorf("failed to create directory: %v", err)
	}

	return fullPath, nil
}

// ListFilesRecursively lists all files and directories recursively.
func ListFilesRecursively(dirPath string, paths *[]string, onFindPath func(filePath string) string) error {
	// Read the directory
	entries, err := os.ReadDir(dirPath)
	if err != nil {
		return fmt.Errorf("failed to read directory %s: %v", dirPath, err)
	}

	// Iterate over the directory entries
	for _, entry := range entries {
		// Get the full path of the entry
		fullPath := filepath.Join(dirPath, entry.Name())

		// If the entry is a directory, recursively list its contents
		if entry.IsDir() {
			err := ListFilesRecursively(fullPath, paths, onFindPath)
			if err != nil {
				return err
			}
		} else {
			// Append the path to the slice
			*paths = append(*paths, onFindPath(fullPath))
		}
	}

	return nil
}

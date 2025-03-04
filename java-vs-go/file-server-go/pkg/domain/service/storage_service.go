package service

import (
	"file-server-go/gen"
	"file-server-go/pkg/env"
	"file-server-go/pkg/utils"
	"fmt"
	"github.com/labstack/gommon/log"
	"io"
	"mime/multipart"
	"os"
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
}

func (_ _storageServiceImpl) SaveFile(
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

	appEnv := env.GetAppEnv()

	filePath := utils.NormalizeFileName(appEnv.StoragePath, dirName+fileName, fileExtension)
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

	fullFileName := fmt.Sprintf("%s/%s./%s", dirName, fileName, fileExtension)

	// Generate file metadata for the response
	fileInfo := map[string]interface{}{
		"fileName":        fileName,
		"fileDownloadUri": fmt.Sprintf("file/download/%s", fullFileName),
		"contentType":     fileExtension,
		"size":            utils.GetFileSize(fileHeader),
		"extension":       fileExtension,
	}

	return fileInfo, nil
}

var (
	storageServiceOnce     sync.Once
	storageServiceInstance StorageService
)

func GetStorageService() StorageService {
	storageServiceOnce.Do(func() {
		storageServiceInstance = &_storageServiceImpl{}
	})
	return storageServiceInstance
}

func (_ _storageServiceImpl) DownloadFile(filePath string) (multipart.File, error) {
	//TODO implement me
	panic("implement me")
}

func (_ _storageServiceImpl) List(dirPath string) (gen.Data, error) {
	//TODO implement me
	panic("implement me")
}

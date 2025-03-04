package handler

import (
	"file-server-go/gen"
	"file-server-go/pkg/domain/service"
	"file-server-go/pkg/provider"
	"fmt"
	"github.com/labstack/gommon/log"
	"github.com/softwareplace/http-utils/api_context"
	"io"
	"mime/multipart"
	"path/filepath"
)

type FileHandler struct {
	storageService service.StorageService
}

func (r FileHandler) DownloadFileRequest(
	ctx *api_context.ApiRequestContext[*api_context.DefaultContext],
) {
	// Get the filePath from query parameters
	queryParams := ctx.QueryValues["filePath"]
	if queryParams == nil || len(queryParams) == 0 {
		ctx.BadRequest("filePath query parameter is required")
		return
	}

	filePath := queryParams[0]

	// Download the file
	file, err := r.storageService.DownloadFile(filePath)
	if err != nil {
		ctx.InternalServerError("Failed to download file: " + err.Error())
		return
	}
	defer func(file multipart.File) {
		err := file.Close()
		if err != nil {
			log.Errorf("Failed to close file: %v", err)
		}
	}(file)

	// Set the appropriate headers for the file download
	fileBase := filepath.Base(filePath)
	(*ctx.Writer).Header().Set("Content-Disposition", fmt.Sprintf("attachment; filename=%s", fileBase))
	(*ctx.Writer).Header().Set("Content-Type", "application/octet-stream")

	// Stream the file to the client
	_, err = io.Copy(*ctx.Writer, file)
	if err != nil {
		ctx.InternalServerError("Failed to stream file: " + err.Error())
		return
	}
}

func (r FileHandler) UploadFileRequest(
	_ gen.UploadFileMultipartBody,
	ctx *api_context.ApiRequestContext[*api_context.DefaultContext],
) {
	// Parse the multipart form
	err := ctx.Request.ParseMultipartForm(10 << provider.AppEnv.MaxFileSize)
	if err != nil {
		ctx.BadRequest("Failed to parse multipart form")
		return
	}

	// Extract the file from the form
	file, fileHeader, err := ctx.Request.FormFile("resource")
	if err != nil {
		ctx.BadRequest("Failed to retrieve resource file from form")
		return
	}
	defer func(file multipart.File) {
		err := file.Close()
		if err != nil {
			log.Errorf("Failed to close file: %v", err)
		}
	}(file)

	// Extract other form fields
	dirName := ctx.Request.FormValue("dirName")
	fileName := ctx.Request.FormValue("fileName")

	if dirName == "" || fileName == "" {
		ctx.BadRequest("dirName and fileName are required")
		return
	}

	fileInfo, err := r.storageService.SaveFile(file, fileHeader, dirName, fileName)
	if err != nil {
		ctx.InternalServerError("Failed to save file: " + err.Error())
		return
	}

	ctx.Ok(fileInfo)
}

func (r FileHandler) ListRequest(ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	queryParams := ctx.QueryValues["resource"]

	dirName := "/"

	if queryParams != nil {
		dirName = queryParams[0]
	}

	list, err := r.storageService.List(dirName)

	if err != nil {
		ctx.InternalServerError("Failed to list files: " + err.Error())
		return
	}
	ctx.Ok(list)
}

package handler

import (
	"file-server-go/gen"
	"file-server-go/pkg/domain/service"
	"file-server-go/pkg/provider"
	"github.com/labstack/gommon/log"
	"github.com/softwareplace/http-utils/api_context"
	"mime/multipart"
)

type FileHandler struct {
	storageService service.StorageService
}

func (r FileHandler) DownloadFileRequest(ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	//TODO implement me
	panic("implement me")
}

func (r FileHandler) UploadFileRequest(_ gen.UploadFileMultipartBody, ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
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
	//TODO implement me
	panic("implement me")
}

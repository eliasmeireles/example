package handler

import (
	"file-server-go/gen"
	"file-server-go/pkg/domain/service"
	"file-server-go/pkg/provider"
	"fmt"
	"github.com/labstack/gommon/log"
	goservecontext "github.com/softwareplace/goserve/context"
	"io"
	"mime/multipart"
	"path/filepath"
	"time"
)

type FileHandler struct {
	storageService service.StorageService
}

func (r resourceHandler) Delete(
	request gen.DeleteClientRequest,
	ctx *goservecontext.Request[*goservecontext.DefaultContext],
) {
	err := r.storageService.Delete(request.FilePath)
	if err != nil {
		ctx.InternalServerError("Failed to delete files: " + err.Error())
		return
	}

	now := time.Now()

	timestamp := int(now.Unix())

	response := map[string]interface{}{
		"code":      200,
		"success":   true,
		"message":   " deleted successfully.",
		"timestamp": timestamp,
	}

	ctx.Ok(response)
}

func (r resourceHandler) List(
	request gen.ListClientRequest,
	ctx *goservecontext.Request[*goservecontext.DefaultContext],
) {
	dirName := "/"

	if request.Resource != "" {
		dirName = request.Resource
	}

	list, err := r.storageService.List(dirName)

	if err != nil {
		ctx.InternalServerError("Failed to list files: " + err.Error())
		return
	}
	ctx.Ok(list)
}

func (r resourceHandler) DownloadFile(
	request gen.DownloadFileClientRequest,
	ctx *goservecontext.Request[*goservecontext.DefaultContext],
) {
	// Download the file
	file, err := r.storageService.DownloadFile(request.FilePath)
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
	fileBase := filepath.Base(request.FilePath)
	(*ctx.Writer).Header().Set("Content-Disposition", fmt.Sprintf("attachment; filename=%s", fileBase))
	(*ctx.Writer).Header().Set("Content-Type", "application/octet-stream")

	// Stream the file to the client
	_, err = io.Copy(*ctx.Writer, file)
	if err != nil {
		ctx.InternalServerError("Failed to stream file: " + err.Error())
		return
	}
}

func (r resourceHandler) UploadFile(
	request gen.UploadFileClientRequest,
	ctx *goservecontext.Request[*goservecontext.DefaultContext],
) {

	// Parse the multipart form
	err := ctx.Request.ParseMultipartForm(10 << provider.AppEnv.MaxFileSize)
	if err != nil {
		ctx.BadRequest("Failed to parse multipart form")
		return
	}

	// Extract the file from the form
	file, fileHeader, err := ctx.FormFile("resource")
	if err != nil {
		ctx.BadRequest("Failed to retrieve resource file from form")
		return
	}

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

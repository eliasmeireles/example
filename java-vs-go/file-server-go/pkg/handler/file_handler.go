package handler

import (
	"file-server-go/gen"
	"github.com/softwareplace/http-utils/api_context"
)

type FileHandler struct {
}

func (r FileHandler) DownloadFileRequest(ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	//TODO implement me
	panic("implement me")
}

func (r FileHandler) UploadFileRequest(requestBody gen.UploadFileMultipartBody, ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	//TODO implement me
	panic("implement me")
}

func (r FileHandler) ListRequest(ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	//TODO implement me
	panic("implement me")
}

package handler

import (
	"file-server-go/gen"
	"github.com/softwareplace/http-utils/api_context"

	"time"
)

type HealthHandler struct{}

func (r HealthHandler) HealthGetRequest(ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	now := time.Now()

	timestamp := int(now.Unix())

	response := gen.Response{
		Message:   "Application is running",
		Timestamp: int64(timestamp),
		Success:   true,
	}

	ctx.Ok(response)
}

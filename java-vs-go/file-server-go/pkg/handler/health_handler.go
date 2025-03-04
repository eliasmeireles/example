package handler

import (
	"github.com/softwareplace/http-utils/api_context"
	"github.com/softwareplace/http-utils/test/gen"
	"time"
)

type HealthHandler struct{}

func (r HealthHandler) HealthGetRequest(ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	message := "Application is running"
	code := 200

	now := time.Now()

	timestamp := int(now.Unix())
	success := true
	response := gen.BaseResponse{
		Message:   &message,
		Code:      &code,
		Timestamp: &timestamp,
		Success:   &success,
	}
	ctx.Ok(response)
}

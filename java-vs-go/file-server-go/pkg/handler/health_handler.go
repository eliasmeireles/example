package handler

import (
	"file-server-go/gen"
	"file-server-go/pkg/domain/model"
	goservecontext "github.com/softwareplace/goserve/context"

	"time"
)

type HealthHandler struct{}

func (r resourceHandler) HealthGet(ctx *goservecontext.Request[*model.User]) {
	now := time.Now()

	timestamp := int(now.Unix())

	response := gen.Response{
		Message:   "Application is running",
		Timestamp: int64(timestamp),
		Success:   true,
	}

	ctx.Ok(response)
}

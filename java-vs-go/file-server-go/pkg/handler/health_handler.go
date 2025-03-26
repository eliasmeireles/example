package handler

import (
	"file-server-go/gen"
	goservecontext "github.com/softwareplace/goserve/context"

	"time"
)

type HealthHandler struct{}

func (r resourceHandler) HealthGet(ctx *goservecontext.Request[*goservecontext.DefaultContext]) {
	now := time.Now()

	timestamp := int(now.Unix())

	response := gen.Response{
		Message:   "Application is running",
		Timestamp: int64(timestamp),
		Success:   true,
	}

	ctx.Ok(response)
}

package handler

import (
	"file-server-go/gen"
	"file-server-go/pkg/provider"
	apicontext "github.com/softwareplace/goserve/context"
	"github.com/softwareplace/goserve/server"
	"sync"
)

type resourceHandler struct {
	AuthorizationHandler
	HealthHandler
	FileHandler
}

var (
	instance resourceHandler
	once     sync.Once
)

func New() gen.ApiRequestService[*apicontext.DefaultContext] {
	once.Do(func() {
		instance = resourceHandler{
			AuthorizationHandler: authorizationHandler(),
			FileHandler:          fileHandler(),
		}
	})

	return instance
}

func authorizationHandler() AuthorizationHandler {
	return AuthorizationHandler{
		loginService: provider.UserService,
	}
}

func fileHandler() FileHandler {
	return FileHandler{
		storageService: provider.StorageService,
	}
}

func EmbeddedServer(
	handler server.Api[*apicontext.DefaultContext],
) {
	gen.RequestServiceHandler(handler, New())
}

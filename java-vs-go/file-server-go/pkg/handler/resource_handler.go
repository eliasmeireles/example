package handler

import (
	"sync"

	"github.com/softwareplace/goserve/server"

	"file-server-go/gen"
	"file-server-go/pkg/domain/model"
	"file-server-go/pkg/provider"
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

func New() gen.ApiRequestService[*model.User] {
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
	handler server.Api[*model.User],
) {
	gen.RequestServiceHandler(handler, New())
}

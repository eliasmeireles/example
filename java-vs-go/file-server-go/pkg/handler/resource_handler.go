package handler

import (
	"file-server-go/gen"
	"file-server-go/pkg/provider"
	"github.com/softwareplace/http-utils/api_context"
	"github.com/softwareplace/http-utils/server"
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

func getResourceHandler() gen.ServiceRequestHandler[*api_context.DefaultContext] {
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
	apiRouterHandler server.ApiRouterHandler[*api_context.DefaultContext],
) {
	apiResourceHandler := gen.ApiResourceHandler(getResourceHandler())
	apiRouterHandler.EmbeddedServer(apiResourceHandler)
}

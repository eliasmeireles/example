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
			AuthorizationHandler: AuthorizationHandler{
				loginService: provider.UserService,
			},
		}
	})

	return instance
}

func EmbeddedServer(
	apiRouterHandler server.ApiRouterHandler[*api_context.DefaultContext],
) {
	apiResourceHandler := gen.ApiResourceHandler(getResourceHandler())
	apiRouterHandler.EmbeddedServer(apiResourceHandler)
}

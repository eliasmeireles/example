package main

import (
	"file-server-go/pkg/handler"
	"file-server-go/pkg/provider"
	"github.com/softwareplace/http-utils/server"
)

func main() {
	appEnv := provider.AppEnv

	server.Default().
		WithPort(appEnv.Port).
		WithContextPath(appEnv.ContextPath).
		WithLoginResource(provider.UserService).
		WithPrincipalService(provider.PrincipalService).
		EmbeddedServer(handler.EmbeddedServer).
		SwaggerDocHandler(appEnv.OpenapiResourcePath).
		StartServer()
}

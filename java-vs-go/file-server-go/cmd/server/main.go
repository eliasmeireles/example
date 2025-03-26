package main

import (
	"file-server-go/pkg/handler"
	"file-server-go/pkg/provider"
	"github.com/softwareplace/goserve/logger"
	"github.com/softwareplace/goserve/server"
)

func init() {
	logger.LogSetup()
}

func main() {
	appEnv := provider.AppEnv

	server.Default().
		LoginService(provider.UserService).
		PrincipalService(provider.PrincipalService).
		EmbeddedServer(handler.EmbeddedServer).
		SwaggerDocHandler(appEnv.OpenapiResourcePath).
		StartServer()
}

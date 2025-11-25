package main

import (
	"github.com/softwareplace/goserve/logger"
	"github.com/softwareplace/goserve/server"

	"file-server-go/pkg/domain/model"
	"file-server-go/pkg/handler"
	"file-server-go/pkg/provider"
)

func init() {
	logger.LogSetup()
}

func main() {
	appEnv := provider.AppEnv

	server.New[*model.User]().
		LoginService(provider.UserService).
		SecurityService(provider.SecurityService).
		EmbeddedServer(handler.EmbeddedServer).
		SwaggerDocHandler(appEnv.OpenapiResourcePath).
		StartServer()
}

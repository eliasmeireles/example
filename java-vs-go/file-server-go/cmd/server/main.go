package main

import (
	"file-server-go/pkg/domain/model"
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

	server.New[*model.User]().
		LoginService(provider.UserService).
		SecurityService(provider.SecurityService).
		EmbeddedServer(handler.EmbeddedServer).
		SwaggerDocHandler(appEnv.OpenapiResourcePath).
		StartServer()
}

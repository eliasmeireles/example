package provider

import (
	"file-server-go/pkg/domain/service"
	appsecurity "file-server-go/pkg/domain/service/security"
	"file-server-go/pkg/env"
	error2 "file-server-go/pkg/errorhandler"
	"github.com/softwareplace/goserve/security"
)

var (
	AppEnv           = env.GetAppEnv()
	PrincipalService = appsecurity.New()
	SecurityService  = security.New(AppEnv.ApiSecretAuthorization, PrincipalService, error2.New())
	UserService      = service.GetLoginService(SecurityService)
	StorageService   = service.GetStorageService()
)

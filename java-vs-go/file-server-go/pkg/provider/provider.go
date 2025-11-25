package provider

import (
	"github.com/softwareplace/goserve/security"

	"file-server-go/pkg/domain/service"
	appsecurity "file-server-go/pkg/domain/service/security"
	"file-server-go/pkg/env"
)

var (
	AppEnv           = env.GetAppEnv()
	PrincipalService = appsecurity.New()
	SecurityService  = security.New(PrincipalService)
	UserService      = service.GetLoginService(SecurityService)
	StorageService   = service.GetStorageService()
)

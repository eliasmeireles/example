package provider

import (
	"file-server-go/pkg/domain/service"
	appsecurity "file-server-go/pkg/domain/service/security"
	"file-server-go/pkg/env"
	"github.com/softwareplace/http-utils/security"
)

var (
	AppEnv           = env.GetAppEnv()
	PrincipalService = appsecurity.GetPrincipalService()
	SecurityService  = security.ApiSecurityServiceBuild(AppEnv.ApiSecretAuthorization, PrincipalService)
	UserService      = service.GetLoginService(SecurityService)
)

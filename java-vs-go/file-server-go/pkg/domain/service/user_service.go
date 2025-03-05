package service

import (
	"file-server-go/pkg/domain/repository"
	"github.com/softwareplace/http-utils/api_context"
	"github.com/softwareplace/http-utils/security"
	"github.com/softwareplace/http-utils/server"
	"sync"
	"time"
)

type userLoginServiceImpl struct {
	server.DefaultPasswordValidator[*api_context.DefaultContext]
	securityService security.ApiSecurityService[*api_context.DefaultContext]
	repository      repository.UserRepository
}

var (
	loginServiceOnce     sync.Once
	loginServiceInstance server.LoginService[*api_context.DefaultContext]
)

func GetLoginService(securityService security.ApiSecurityService[*api_context.DefaultContext]) server.LoginService[*api_context.DefaultContext] {
	loginServiceOnce.Do(func() {
		loginServiceInstance = &userLoginServiceImpl{
			securityService: securityService,
			repository:      repository.NewUserRepository(),
		}
	})

	return loginServiceInstance
}

func (u userLoginServiceImpl) SecurityService() security.ApiSecurityService[*api_context.DefaultContext] {
	return u.securityService
}

func (u userLoginServiceImpl) Login(user server.LoginEntryData) (*api_context.DefaultContext, error) {
	matchingUser, err := u.repository.GetByUsername(user.Username)
	if err != nil {
		return nil, err
	}

	ctx := api_context.NewDefaultCtx()
	ctx.SetEncryptedPassword(matchingUser.Password)
	ctx.SetRoles(matchingUser.Roles...)

	return ctx, nil
}

func (u userLoginServiceImpl) TokenDuration() time.Duration {
	return 1 * time.Hour
}

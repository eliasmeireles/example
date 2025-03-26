package service

import (
	"file-server-go/pkg/domain/repository"
	apicontext "github.com/softwareplace/goserve/context"
	"github.com/softwareplace/goserve/security"
	"github.com/softwareplace/goserve/security/login"
	"sync"
	"time"
)

type userLoginServiceImpl struct {
	login.DefaultPasswordValidator[*apicontext.DefaultContext]
	securityService security.Service[*apicontext.DefaultContext]
	repository      repository.UserRepository
}

var (
	loginServiceOnce     sync.Once
	loginServiceInstance login.Service[*apicontext.DefaultContext]
)

func GetLoginService(securityService security.Service[*apicontext.DefaultContext]) login.Service[*apicontext.DefaultContext] {
	loginServiceOnce.Do(func() {
		loginServiceInstance = &userLoginServiceImpl{
			securityService: securityService,
			repository:      repository.NewUserRepository(),
		}
	})

	return loginServiceInstance
}

func (u userLoginServiceImpl) SecurityService() security.Service[*apicontext.DefaultContext] {
	return u.securityService
}

func (u userLoginServiceImpl) Login(user login.User) (*apicontext.DefaultContext, error) {
	matchingUser, err := u.repository.GetByUsername(user.Username)
	if err != nil {
		return nil, err
	}

	ctx := apicontext.NewDefaultCtx()
	ctx.SetEncryptedPassword(matchingUser.Password)
	ctx.SetRoles(matchingUser.Roles...)

	return ctx, nil
}

func (u userLoginServiceImpl) TokenDuration() time.Duration {
	return 1 * time.Hour
}

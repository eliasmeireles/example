package service

import (
	"file-server-go/pkg/domain/repository"
	goservecontext "github.com/softwareplace/goserve/context"
	"github.com/softwareplace/goserve/security"
	"github.com/softwareplace/goserve/security/login"
	"sync"
	"time"
)

type userLoginServiceImpl struct {
	login.DefaultPasswordValidator[*goservecontext.DefaultContext]
	securityService security.Service[*goservecontext.DefaultContext]
	repository      repository.UserRepository
}

var (
	loginServiceOnce     sync.Once
	loginServiceInstance login.Service[*goservecontext.DefaultContext]
)

func GetLoginService(securityService security.Service[*goservecontext.DefaultContext]) login.Service[*goservecontext.DefaultContext] {
	loginServiceOnce.Do(func() {
		loginServiceInstance = &userLoginServiceImpl{
			securityService: securityService,
			repository:      repository.NewUserRepository(),
		}
	})

	return loginServiceInstance
}

func (u userLoginServiceImpl) SecurityService() security.Service[*goservecontext.DefaultContext] {
	return u.securityService
}

func (u userLoginServiceImpl) Login(user login.User) (*goservecontext.DefaultContext, error) {
	matchingUser, err := u.repository.GetByUsername(user.Username)
	if err != nil {
		return nil, err
	}

	ctx := goservecontext.NewDefaultCtx()
	ctx.SetEncryptedPassword(matchingUser.Password)
	ctx.SetRoles(matchingUser.Roles...)

	return ctx, nil
}

func (u userLoginServiceImpl) TokenDuration() time.Duration {
	return 1 * time.Hour
}

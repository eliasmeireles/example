package service

import (
	"fmt"
	"sync"
	"time"

	"github.com/softwareplace/goserve/security"
	"github.com/softwareplace/goserve/security/login"

	"file-server-go/pkg/domain/model"
	"file-server-go/pkg/domain/repository"
)

type userLoginServiceImpl struct {
	login.DefaultPasswordValidator[*model.User]
	securityService security.Service[*model.User]
	repository      repository.UserRepository
}

var (
	loginServiceOnce     sync.Once
	loginServiceInstance login.Service[*model.User]
)

func GetLoginService(securityService security.Service[*model.User]) login.Service[*model.User] {
	loginServiceOnce.Do(func() {
		loginServiceInstance = &userLoginServiceImpl{
			securityService: securityService,
			repository:      repository.NewUserRepository(),
		}
	})

	return loginServiceInstance
}

func (u userLoginServiceImpl) SecurityService() security.Service[*model.User] {
	return u.securityService
}

func (u userLoginServiceImpl) Login(user login.User) (*model.User, error) {
	matchingUser, err := u.repository.GetByUsername(user.Username)
	if err != nil {
		return nil, err
	}

	if u.IsValidPassword(user, &matchingUser) {
		return &matchingUser, nil
	}

	return nil, fmt.Errorf("invalid username or password")
}

func (u userLoginServiceImpl) TokenDuration(principal *model.User) time.Duration {
	return 1 * time.Hour
}

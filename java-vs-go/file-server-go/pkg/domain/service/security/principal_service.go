package security

import (
	"sync"

	goservecontext "github.com/softwareplace/goserve/context"
	"github.com/softwareplace/goserve/security/principal"

	"file-server-go/pkg/domain/model"
	"file-server-go/pkg/domain/repository"
)

type principalServiceImpl struct {
	repository repository.UserRepository
}

var (
	principalServiceOnce     sync.Once
	principalServiceInstance principal.Service[*model.User]
)

func New() principal.Service[*model.User] {
	principalServiceOnce.Do(func() {
		principalServiceInstance = &principalServiceImpl{
			repository: repository.NewUserRepository(),
		}
	})
	return principalServiceInstance
}

func (p principalServiceImpl) LoadPrincipal(ctx *goservecontext.Request[*model.User]) bool {
	matchingUser, err := p.repository.GetByAuthToken(ctx.AccessId)

	if err != nil {
		return false
	}

	m := &matchingUser
	ctx.Principal = &m

	return true
}

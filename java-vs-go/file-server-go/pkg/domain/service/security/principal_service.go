package security

import (
	"file-server-go/pkg/domain/repository"
	goservecontext "github.com/softwareplace/goserve/context"
	"github.com/softwareplace/goserve/security/principal"
	"sync"
)

type principalServiceImpl struct {
	repository repository.UserRepository
}

var (
	principalServiceOnce     sync.Once
	principalServiceInstance principal.Service[*goservecontext.DefaultContext]
)

func New() principal.Service[*goservecontext.DefaultContext] {
	principalServiceOnce.Do(func() {
		principalServiceInstance = &principalServiceImpl{
			repository: repository.NewUserRepository(),
		}
	})
	return principalServiceInstance
}

func (p principalServiceImpl) LoadPrincipal(ctx *goservecontext.Request[*goservecontext.DefaultContext]) bool {
	matchingUser, err := p.repository.GetByAuthToken(ctx.AccessId)

	if err != nil {
		return false
	}

	context := goservecontext.NewDefaultCtx()
	context.SetRoles(matchingUser.Roles...)
	context.SetEncryptedPassword(matchingUser.Password)
	ctx.Principal = &context
	return true
}

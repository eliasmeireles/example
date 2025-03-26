package security

import (
	"file-server-go/pkg/domain/repository"
	apicontext "github.com/softwareplace/goserve/context"
	"github.com/softwareplace/goserve/security/principal"
	"sync"
)

type principalServiceImpl struct {
	repository repository.UserRepository
}

var (
	principalServiceOnce     sync.Once
	principalServiceInstance principal.Service[*apicontext.DefaultContext]
)

func New() principal.Service[*apicontext.DefaultContext] {
	principalServiceOnce.Do(func() {
		principalServiceInstance = &principalServiceImpl{
			repository: repository.NewUserRepository(),
		}
	})
	return principalServiceInstance
}

func (p principalServiceImpl) LoadPrincipal(ctx *apicontext.Request[*apicontext.DefaultContext]) bool {
	matchingUser, err := p.repository.GetByAuthToken(ctx.AccessId)

	if err != nil {
		return false
	}

	context := apicontext.NewDefaultCtx()
	context.SetRoles(matchingUser.Roles...)
	context.SetEncryptedPassword(matchingUser.Password)
	ctx.Principal = &context
	return true
}

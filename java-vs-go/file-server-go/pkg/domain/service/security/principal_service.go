package security

import (
	"file-server-go/pkg/domain/repository"
	"github.com/softwareplace/http-utils/api_context"
	"github.com/softwareplace/http-utils/security/principal"
	"sync"
)

type principalServiceImpl struct {
	repository repository.UserRepository
}

var (
	principalServiceOnce     sync.Once
	principalServiceInstance principal.PService[*api_context.DefaultContext]
)

func GetPrincipalService() principal.PService[*api_context.DefaultContext] {
	principalServiceOnce.Do(func() {
		principalServiceInstance = &principalServiceImpl{
			repository: repository.NewUserRepository(),
		}
	})
	return principalServiceInstance
}

func (p principalServiceImpl) LoadPrincipal(ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) bool {
	matchingUser, err := p.repository.GetByAuthToken(ctx.AccessId)

	if err != nil {
		return false
	}

	context := api_context.NewDefaultCtx()
	context.SetRoles(matchingUser.Roles...)
	context.SetEncryptedPassword(matchingUser.Password)
	ctx.Principal = &context
	return true
}

package handler

import (
	"file-server-go/gen"
	"github.com/softwareplace/http-utils/api_context"
	"github.com/softwareplace/http-utils/server"
	"log"
	"time"
)

type AuthorizationHandler struct {
	loginService server.LoginService[*api_context.DefaultContext]
}

func (r AuthorizationHandler) GetAuthorizationRequest(requestBody gen.UserInfo, ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	r._authorizationGenerator(requestBody, ctx)
}

func (r AuthorizationHandler) AuthorizationGenRequest(requestBody gen.UserInfo, ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	r._authorizationGenerator(requestBody, ctx)
}

func (r AuthorizationHandler) _authorizationGenerator(requestBody gen.UserInfo, ctx *api_context.ApiRequestContext[*api_context.DefaultContext]) {
	login, err := r.loginService.Login(server.LoginEntryData{
		Username: requestBody.Username,
		Password: requestBody.Password,
	})

	if err != nil {
		ctx.Forbidden("Invalid username or password")
		return
	}
	jwt, err := r.loginService.SecurityService().GenerateJWT(login, r.loginService.TokenDuration())

	if err != nil {
		log.Printf("LOGIN/JWT: Failed to generate JWT: %v", err)
		ctx.InternalServerError("Login failed with internal server error. Please try again later.")
		return
	}

	now := time.Now()

	timestamp := int(now.Unix())

	response := map[string]interface{}{
		"code":      200,
		"success":   true,
		"jwt":       jwt.Token,
		"expires":   jwt.Expires,
		"message":   "Authorization successful.",
		"timestamp": timestamp,
	}

	ctx.Ok(response)
}

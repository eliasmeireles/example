package handler

import (
	"file-server-go/gen"
	goservecontext "github.com/softwareplace/goserve/context"
	"github.com/softwareplace/goserve/security/login"
)

type AuthorizationHandler struct {
	loginService login.Service[*goservecontext.DefaultContext]
}

func (r resourceHandler) GetAuthorization(request gen.GetAuthorizationClientRequest, ctx *goservecontext.Request[*goservecontext.DefaultContext]) {
	r._authorizationGenerator(request.Body, ctx)
}

func (r resourceHandler) AuthorizationGen(request gen.AuthorizationGenClientRequest, ctx *goservecontext.Request[*goservecontext.DefaultContext]) {
	r._authorizationGenerator(request.Body, ctx)
}

func (r AuthorizationHandler) _authorizationGenerator(requestBody gen.UserInfo, ctx *goservecontext.Request[*goservecontext.DefaultContext]) {
	//login, err := r.loginService.Login(login.User{
	//	Username: requestBody.Username,
	//	Password: requestBody.Password,
	//})
	//
	//if err != nil {
	//	ctx.Forbidden("Invalid username or password")
	//	return
	//}
	//jwt, err := r.loginService.SecurityService().GenerateJWT(login, r.loginService.TokenDuration())
	//
	//if err != nil {
	//	log.Printf("LOGIN/JWT: Failed to generate JWT: %v", err)
	//	ctx.InternalServerError("Login failed with internal server errorhandler. Please try again later.")
	//	return
	//}
	//
	//now := time.Now()
	//
	//timestamp := int(now.Unix())
	//
	//response := map[string]interface{}{
	//	"code":      200,
	//	"success":   true,
	//	"jwt":       jwt.Token,
	//	"expires":   jwt.Expires,
	//	"message":   "Authorization successful.",
	//	"timestamp": timestamp,
	//}
	//
	//ctx.Ok(response)

	ctx.Forbidden("Not implemented")
}

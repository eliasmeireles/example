package env

import (
	"flag"
	"os"
	"strings"
)

type AppEnv struct {
	Port                      string
	ContextPath               string
	OpenapiResourcePath       string
	StoragePath               string
	ApiSecretAuthorization    string
	AuthorizationResourcePath string
}

var env *AppEnv

func SetAppEnv(appEnv *AppEnv) {
	env = appEnv
}

func GetAppEnv() *AppEnv {
	if env == nil {
		contextPath := os.Getenv("CONTEXT_PATH")
		storagePath := os.Getenv("ENV_STORAGE_PATH")
		apiSecretAuthorization := os.Getenv("API_SECRET_AUTHORIZATION")
		openapiResourcePath := os.Getenv("OPENAPI_RESOURCE_PATH")
		authorizationResourcePath := os.Getenv("AUTHORIZATION_RESOURCE_PATH")
		port := os.Getenv("PORT")

		if storagePath == "" {
			storagePath = os.Getenv("HOME") + "/file-server/go/"
		}

		if authorizationResourcePath == "" {
			panic("Env AUTHORIZATION_RESOURCE_PATH is required but not set")
		}

		if apiSecretAuthorization == "" {
			panic("Env API_SECRET_AUTHORIZATION is required but not set")
		}

		if openapiResourcePath == "" {
			openapiResourcePath = "resources/openapi.yaml"
		}

		if contextPath == "" {
			contextPath = "/api/file-server/v1/"
		}

		if port == "" {
			port = "8080"
		}

		flag.Parse()

		env = &AppEnv{
			Port:                      port,
			ContextPath:               strings.TrimSuffix(contextPath, "/") + "/",
			OpenapiResourcePath:       openapiResourcePath,
			StoragePath:               storagePath,
			ApiSecretAuthorization:    apiSecretAuthorization,
			AuthorizationResourcePath: authorizationResourcePath,
		}
	}
	return env
}

package env

import (
	"file-server-go/pkg/utils"
	"flag"
	"os"
	"strconv"
	"strings"
)

type AppEnv struct {
	Port                      string
	ContextPath               string
	OpenapiResourcePath       string
	StoragePath               string
	ApiSecretAuthorization    string
	AuthorizationResourcePath string
	MaxFileSize               int
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
		} else if strings.HasPrefix(storagePath, "~") {
			storagePath = strings.Replace(storagePath, "~", os.Getenv("HOME"), 1)
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
		storagePath = strings.TrimSuffix(storagePath, "/")

		_, err := utils.CreatDirs(storagePath)

		if err != nil {
			panic(err)
		}

		env = &AppEnv{
			Port:                      port,
			ContextPath:               strings.TrimSuffix(contextPath, "/") + "/",
			OpenapiResourcePath:       openapiResourcePath,
			StoragePath:               storagePath,
			ApiSecretAuthorization:    apiSecretAuthorization,
			AuthorizationResourcePath: authorizationResourcePath,
			MaxFileSize:               getMaxFileSize(),
		}
	}
	return env
}

// getMaxFileSize returns the maximum allowed file size in MB.
// If the MAX_FILE_SIZE environment variable is not set or invalid, it defaults to 120 MB.
func getMaxFileSize() int {
	// Get the MAX_FILE_SIZE environment variable
	maxFileSizeStr := os.Getenv("MAX_FILE_SIZE")

	// If the environment variable is not set, return the default value (120 MB)
	if maxFileSizeStr == "" {
		return 120
	}

	// Try to parse the environment variable as an integer
	maxFileSize, err := strconv.Atoi(maxFileSizeStr)
	if err != nil || maxFileSize <= 0 {
		// If parsing fails or the value is invalid, return the default value (120 MB)
		return 120
	}

	// Return the parsed value
	return maxFileSize
}

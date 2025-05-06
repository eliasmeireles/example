package env

import (
	"file-server-go/pkg/utils"
	"flag"
	"os"
	"strconv"
	"strings"
)

type AppEnv struct {
	OpenapiResourcePath       string
	StoragePath               string
	AuthorizationResourcePath string
	MaxFileSize               int
}

var env *AppEnv

func SetAppEnv(appEnv *AppEnv) {
	env = appEnv
}

func GetAppEnv() *AppEnv {
	if env == nil {
		storagePath := os.Getenv("ENV_STORAGE_PATH")
		openapiResourcePath := os.Getenv("OPENAPI_RESOURCE_PATH")
		authorizationResourcePath := os.Getenv("AUTHORIZATION_RESOURCE_PATH")

		if storagePath == "" {
			storagePath = os.Getenv("HOME") + "/file-server/go/"
		} else if strings.HasPrefix(storagePath, "~") {
			storagePath = strings.Replace(storagePath, "~", os.Getenv("HOME"), 1)
		}

		if authorizationResourcePath == "" {
			panic("Env AUTHORIZATION_RESOURCE_PATH is required but not set")
		}

		if openapiResourcePath == "" {
			openapiResourcePath = "resources/openapi.yaml"
		}
		flag.Parse()
		storagePath = strings.TrimSuffix(storagePath, "/")

		_, err := utils.CreatDirs(storagePath)

		if err != nil {
			panic(err)
		}

		env = &AppEnv{
			OpenapiResourcePath:       openapiResourcePath,
			StoragePath:               storagePath,
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

package utils

import "os"

func AppPort() string {
	return GetEnvOrDefault("APP_PORT", "1032")
}

func GetEnvOrDefault(env string, defaultValue string) string {
	value, exists := os.LookupEnv(env)
	if !exists || value == "" {
		return defaultValue
	}
	return value
}

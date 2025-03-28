package utils

import "os"

func GetEnvOrDefault(env string, defaultValue string) string {
	value, exists := os.LookupEnv(env)
	if !exists || value == "" {
		return defaultValue
	}
	return value
}

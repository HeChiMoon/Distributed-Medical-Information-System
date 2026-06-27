package com.dmis.common.api;

import java.util.List;

public record ModuleInfo(String serviceName, String domain, List<String> capabilities) {
}

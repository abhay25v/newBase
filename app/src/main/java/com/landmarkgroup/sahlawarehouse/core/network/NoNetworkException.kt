package com.landmarkgroup.sahlawarehouse.core.network

import java.io.IOException

class NoNetworkException(message: String = "No internet connection available") : IOException(message)

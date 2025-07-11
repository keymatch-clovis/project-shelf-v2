package com.example.project_shelf.app.use_case

import kotlin.time.Duration.Companion.seconds

// This timeout should not be related to Android timeouts (e.g. Dialogs, Toasts, Snack bars, etc.),
// but should be enough for it to be usable. If we set a timeout too low, then the deletion time
// is almost useless.
val DELETION_TIMEOUT = 20.seconds

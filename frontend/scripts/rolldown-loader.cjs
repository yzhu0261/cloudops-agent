const Module = require('node:module')
const path = require('node:path')

const originalResolveFilename = Module._resolveFilename
Module._extensions['.tmp'] = Module._extensions['.node']

Module._resolveFilename = function resolveRolldownBinding(request, parent, isMain, options) {
  if (request === '@rolldown/binding-win32-x64-msvc') {
    return path.join(
      process.cwd(),
      'node_modules',
      '@rolldown',
      'binding-win32-x64-msvc',
      'binding.tmp',
    )
  }

  return originalResolveFilename.call(this, request, parent, isMain, options)
}

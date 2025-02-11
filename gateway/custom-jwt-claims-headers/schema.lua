local typedefs = require "kong.db.schema.typedefs"

return {
  name = "custom-jwt-claims-headers",
  fields = {
    {
      -- this plugin will only be applied to Services or Routes
      consumer = typedefs.no_consumer
    },
    {
      -- The `config` field must be a `record` type with an array of fields
      config = {
        type = "record",
        fields = {
          -- Defining uri_param_names as an array
          { uri_param_names = { type = "array", default = { "jwt" }, elements = { type = "string" } } },
          -- Defining claims_to_include as an array of strings
          { claims_to_include = { type = "array", default = { ".*" }, elements = { type = "string" } } },
          -- continue_on_error as a boolean
          { continue_on_error = { type = "boolean", default = false } }
        }
      }
    }
  }
}
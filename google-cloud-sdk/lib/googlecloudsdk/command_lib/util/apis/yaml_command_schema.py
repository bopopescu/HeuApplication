# Copyright 2017 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Data objects to support the yaml command schema."""


from enum import Enum

from googlecloudsdk.calliope import actions
from googlecloudsdk.calliope import base
from googlecloudsdk.core import module_util


class Error(Exception):
  """Base class for module errors."""
  pass


class InvalidSchemaError(Error):
  """Error for when a yaml command is malformed."""
  pass


NAME_FORMAT_KEY = '__name__'
REL_NAME_FORMAT_KEY = '__relative_name__'
RESOURCE_TYPE_FORMAT_KEY = '__resource_type__'


class CommandData(object):

  def __init__(self, name, data):
    self.is_hidden = data.get('is_hidden', False)
    self.release_tracks = [
        base.ReleaseTrack.FromId(i) for i in data.get('release_tracks', [])]
    self.command_type = CommandType.ForName(data.get('command_type', name))
    self.help_text = data['help_text']
    self.request = Request(self.command_type, data['request'])
    self.response = Response(data.get('response', {}))
    async_data = data.get('async', None)
    self.async = Async(async_data) if async_data else None
    self.arguments = Arguments(data['arguments'])
    self.input = Input(self.command_type, data.get('input', {}))
    self.output = Output(data.get('output', {}))


class CommandType(Enum):
  """An enum for the types of commands the generator supports.

  Attributes:
    default_method: str, The name of the API method to use by default for this
      type of command.
  """
  DESCRIBE = ('get')
  LIST = ('list')
  DELETE = ('delete')
  # Generic commands are those that don't extend a specific calliope command
  # base class.
  GENERIC = (None)

  def __init__(self, default_method):
    self.default_method = default_method

  @classmethod
  def ForName(cls, name):
    try:
      return CommandType[name.upper()]
    except KeyError:
      return CommandType.GENERIC


class Request(object):

  def __init__(self, command_type, data):
    self.collection = data['collection']
    self.api_version = data.get('api_version', None)
    self.method = data.get('method', command_type.default_method)
    if not self.method:
      raise InvalidSchemaError(
          'request.method was not specified and there is no default for this '
          'command type.')
    self.resource_method_params = data.get('resource_method_params', {})
    self.static_fields = data.get('static_fields', {})
    self.modify_request_hook = Hook.FromData(data, 'modify_request_hook')
    self.create_request_hook = Hook.FromData(data, 'create_request_hook')
    self.issue_request_hook = Hook.FromData(data, 'issue_request_hook')


class Response(object):

  def __init__(self, data):
    self.error = ResponseError(data['error']) if 'error' in data else None


class ResponseError(object):

  def __init__(self, data):
    self.field = data.get('field', 'error')
    self.code = data.get('code', None)
    self.message = data.get('message', None)


class Async(object):

  def __init__(self, data):
    self.collection = data['collection']
    self.api_version = data.get('api_version', None)
    self.method = data.get('method', 'get')
    self.response_name_field = data.get('response_name_field', 'name')
    self.extract_resource_result = data.get('extract_resource_result', True)
    self.resource_get_method = data.get('resource_get_method', 'get')
    self.resource_get_method_params = data.get('resource_get_method_params', {})
    self.result_attribute = data.get('result_attribute', None)
    self.state = AsyncStateField(data.get('state', {}))
    self.error = AsyncErrorField(data.get('error', {}))


class AsyncStateField(object):

  def __init__(self, data):
    self.field = data.get('field', 'done')
    self.success_values = data.get('success_values', [True])
    self.error_values = data.get('error_values', [])


class AsyncErrorField(object):

  def __init__(self, data):
    self.field = data.get('field', 'error')


class Arguments(object):
  """Everything about cli arguments are registered in this section."""

  def __init__(self, data):
    resource = data.get('resource', None)
    self.resource = Resource(resource) if resource else None
    self.additional_arguments_hook = Hook.FromData(
        data, 'additional_arguments_hook')
    self.params = {
        param: Argument.FromData(param, param_data)
        for param, param_data in data.get('params', {}).iteritems()}
    self.mutex_group_params = {}
    for group_id, group_data in enumerate(data.get('mutex_groups', [])):
      group = MutexGroup(group_id, group_data)
      self.mutex_group_params.update({
          param: Argument.FromData(param, param_data, group=group)
          for param, param_data in group_data.get('params', {}).iteritems()})

  def AllArguments(self):
    args = {}
    args.update(self.mutex_group_params)
    args.update(self.params)
    if self.resource:
      args.update(self.resource.params)
    return args


class Resource(object):

  def __init__(self, data):
    self.help_text = data['help_text']
    self.response_id_field = data.get('response_id_field', None)
    self.params = {
        param: Argument.FromData(param, param_data)
        for param, param_data in data.get('params', {}).iteritems()}


class MutexGroup(object):

  def __init__(self, group_id, data):
    self.group_id = group_id
    self.required = data.get('required', False)


class Argument(object):
  """Encapsulates data used to generate arguments."""

  @classmethod
  def FromData(cls, param, data, group=None):
    deprecation = data.get('deprecated', None)
    name = data.get('arg_name', param)
    return Argument(
        name,
        data['help_text'],
        Hook.FromData(data, 'completer'),
        data.get('is_positional', False),
        Hook.FromData(data, 'type'),
        data.get('choices', None),
        data.get('default', None),
        Hook.FromData(data, 'processor'),
        data.get('required', False),
        data.get('hidden', False),
        actions.DeprecationAction(name, **deprecation) if deprecation else None,
        group
    )

  # pylint:disable=redefined-builtin, type param needs to match the schema.
  def __init__(self, arg_name, help_text, completer=None, is_positional=False,
               type=None, choices=None, default=None, processor=None,
               required=False, hidden=False, action=None, group=None):
    self.arg_name = arg_name
    self.help_text = help_text
    self.completer = completer
    self.is_positional = is_positional
    self.type = type
    self.choices = choices
    self.default = default
    self.processor = processor
    self.required = required
    self.hidden = hidden
    self.action = action
    self.group = group


class Input(object):

  def __init__(self, command_type, data):
    self.confirmation_prompt = data.get('confirmation_prompt', None)
    if not self.confirmation_prompt and command_type is CommandType.DELETE:
      self.confirmation_prompt = (
          'You are about to delete {{{}}} [{{{}}}]'.format(
              RESOURCE_TYPE_FORMAT_KEY, NAME_FORMAT_KEY))


class Output(object):

  def __init__(self, data):
    self.format = data.get('format')


class Hook(object):
  """Represents a Python code hook declared in the yaml spec.

  A code hook points to some python element with a module path, and attribute
  path like: package.module:class.attribute.

  If arguments are provided, first the function is called with the arguments
  and the return value of that is the hook that is used. For example:

  googlecloudsdk.calliope.arg_parsers:Duration:lower_bound=1s,upper_bound=1m
  """

  @classmethod
  def FromData(cls, data, key):
    """Gets the hook from the spec data.

    Args:
      data: The yaml spec
      key: The key to extract the hook path from.

    Returns:
      The Python element to call.
    """
    path = data.get(key, None)
    if path:
      return _ImportPythonHook(path).GetHook()
    return None

  def __init__(self, attribute, kwargs=None):
    self.attribute = attribute
    self.kwargs = kwargs

  def GetHook(self):
    """Gets the Python element that corresponds to this hook.

    Returns:
      A Python element.
    """
    if self.kwargs:
      return  self.attribute(**self.kwargs)
    return self.attribute


def _ImportPythonHook(path):
  """Imports the given python hook.

  Depending on what it is used for, a hook is a reference to a class, function,
  or attribute in Python code.

  Args:
    path: str, The path of the hook to import. It must be in the form of:
      package.module:attribute.attribute where the module path is separated from
      the class name and sub attributes by a ':'. Additionally, ":arg=value,..."
      can be appended to call the function with the given args and use the
      return value as the hook.

  Raises:
    InvalidSchemaError: If the given module or attribute cannot be loaded.

  Returns:
    Hook, the hook configuration.
  """
  parts = path.split(':')
  if len(parts) != 2 and len(parts) != 3:
    raise InvalidSchemaError(
        'Invalid Python hook: [{}]. Hooks must be in the format: '
        'package(.module)+:attribute(.attribute)*(:arg=value(,arg=value)*)?'
        .format(path))
  try:
    attr = module_util.ImportModule(parts[0] + ':' + parts[1])
  except module_util.ImportModuleError as e:
    raise InvalidSchemaError(
        'Could not import Python hook: [{}]. {}'.format(path, e))

  kwargs = {}
  if len(parts) == 3:
    for arg in parts[2].split(','):
      arg_parts = arg.split('=')
      if len(arg_parts) != 2:
        raise InvalidSchemaError(
            'Invalid Python hook: [{}]. Args must be in the form arg=value,'
            'arg=value,...'.format(path))
      kwargs[arg_parts[0]] = arg_parts[1]

  return Hook(attr, kwargs)

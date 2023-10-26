from typing import Optional, TypeVar, Generic, Union, List, Dict
from pydantic import BaseModel
from sqlalchemy.util import defaultdict

# Type Variables
T = TypeVar("T")
# Error type: Dictionary with key as string and value as string or list of strings.
E = Dict[str, Union[str, List[str]]]


# API Response Models
class SuccessResponse(BaseModel, Generic[T]):
    success: bool = True
    data: T


class ErrorResponse(BaseModel):
    def __init__(self, errors: Union[E, "ErrorBuilder"]):
        if isinstance(errors, ErrorBuilder):
            errors = errors.serialize()
        super().__init__(errors=errors)

    success: bool = False
    errors: E


APIResponse = Union[SuccessResponse[T], ErrorResponse]


class ErrorBuilder:
    has_error: bool = False

    def __init__(self, model: Optional[BaseModel] = None):
        self._model = model
        self._errors = defaultdict(list)

    def add(self, field: str, message: str):
        if self._model and field != "global":
            if not hasattr(self._model, field):
                raise ValueError(f"{field} is not an attribute of the provided BaseModel")
        self.has_error = True
        self._errors[field].append(message)

    def serialize(self) -> Dict[str, Union[str, List[str]]]:
        # Simplify entries with only one message
        for key, messages in self._errors.items():
            if len(messages) == 1:
                self._errors[key] = messages[0]
        return dict(self._errors)

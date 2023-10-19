using { managed } from '@sap/cds/common';
using { TodoEntryV2.TodoEntryV2 } from './external/TodoEntryV2';

extend TodoEntryV2 with {
    userId: String;
}

@path: 'TodoGeneratorService'
service TodoGeneratorService {
    type GeneratedTodo {
            title : String;
            description : String;
        }
    function getTodoSuggestion() returns GeneratedTodo;
    action addTodo(todo: GeneratedTodo);
    action quit() returns String;
}

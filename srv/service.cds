using { managed } from '@sap/cds/common';
using { TodoEntryV2.TodoEntryV2 } from './external/TodoEntryV2';
using { Goal_101.Goal_101 } from './external/Goal_101';

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

@path: 'GoalService'
service GoalService {
    entity Goal as projection on Goal_101 {
            id,
            name as title,
            metric as description,
            done as completion,
        }
}

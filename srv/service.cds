using { managed } from '@sap/cds/common';
using { TodoEntryV2.TodoEntryV2 } from './external/TodoEntryV2';
using { Goal.Goal_101 } from './external/Goal';

@path: 'SignupService'
service SignupService {
    action signUp(session: String);
}

@path: 'GoalService'
service GoalService {
    entity Goal as projection on Goal_101 {
            id,
            name as title,
            metric as description,
        }
}

extend TodoEntryV2 with {
    userId: String;
}

@path: 'TodoService'
service TodoService {
    entity Todo as projection on TodoEntryV2 {
            todoEntryId as id,
            todoEntryName as title,
            status,
        }
}

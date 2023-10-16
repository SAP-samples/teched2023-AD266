/* global Vue axios */ //> from vue.html
const $ = (sel) => document.querySelector(sel);

const languages = {
  js: {
    url: {
      dev: 'http://localhost:3001',
      prod: 'https://sap-cloud-sdk-js-demo-js-srv.cfapps.sap.hana.ondemand.com'
    },
    name: 'JS',
    key: 'js'
  },
  java: {
    url: {
      dev: 'http://localhost:8080',
      prod: 'https://sap-cloud-sdk-js-demo-java-srv.cfapps.sap.hana.ondemand.com'
    },
    name: 'Java',
    key: 'java'
  }
};
const todoApp = Vue.createApp({
  data() {
    return {
      todos: [],
      user: undefined,
      language: languages.js,
      otherLanguage: languages.java,
      suggestion: { title: '', description: '' }
    };
  },

  methods: {
    async getToDos() {
      const { data } = await axios.get(
        `${todoService}/ToDo?$orderby=modifiedAt`
      );
      todoApp.todos = data.value;
    },

    async getToDoSuggestion() {
      const { data } = await axios.get(
        `${this.getTodoGeneratorService()}/getTodoSuggestion()`
      );
      const { title, description } = data;
      todoApp.suggestion = { title, description };
    },

    getEnv() {
      return location.hostname === 'localhost' ||
        location.hostname === '127.0.0.1'
        ? 'dev'
        : 'prod';
    },

    async quit() {
      if (!confirm('Are you sure?')) return;
      const { data } = await axios.post(
        `${this.getTodoGeneratorService()}/quit`,
        {},
        { 'content-type': 'application/json' }
      );
      alert(data.value);
      todoApp.suggestion = { title: 'Weekend!', description: 'Properly end the week, party!'}
      await this.getToDos();
    },

    async toggleLanguage() {
      const [oldKey, newKey] =
        todoApp.language.name === languages.js.name
          ? ['js', 'java']
          : ['java', 'js'];
      todoApp.language = languages[newKey];
      todoApp.otherLanguage = languages[oldKey];

      await this.getToDoSuggestion();
    },

    async addTodo() {
      await axios.post(
        `${this.getTodoGeneratorService()}/addTodo`,
        { todo: todoApp.suggestion },
        { 'content-type': 'application/json' }
      );
      await this.getToDos();
      await this.regenerateTodo();
    },

    async regenerateTodo() {
      await this.getToDoSuggestion();
    },

    getTodoGeneratorService() {
      return `${
        todoApp.language.url[this.getEnv()]
      }/odata/v4/TodoGeneratorService`;
    }
  }
}).mount('#app');

const todoService = todoApp.getEnv() === 'dev' ? 
'http://localhost:4004/odata/v4/ToDoService' : 'https://sap-cloud-sdk-js-demo-todo-server.cfapps.sap.hana.ondemand.com/odata/v4/ToDoService/';

todoApp.getToDos();
todoApp.getToDoSuggestion();

-- Python
INSERT INTO examen_tecnico (id, id_rol_habilidad) VALUES (1, 39);

INSERT INTO examen_pregunta (id, id_examen_tecnico, pregunta, dificultad) VALUES
(1, 1, 'What does the "len()" function in Python do?', 1),
(2, 1, 'What is a tuple in Python?', 1),
(3, 1, 'How can you improve the performance of a Python application?', 2),
(4, 1, 'What is the difference between "list" and "tuple" in Python?', 2),
(5, 1, 'Explain what a lambda function in Python is.', 3),
(6, 1, 'What is the purpose of the "with" statement in Python?', 3);

INSERT INTO examen_respuesta (id, respuesta, id_examen_pregunta, correcta) VALUES
(1, 'Returns the length of an object', 1, TRUE),
(2, 'Calculates the sum of elements in an object', 1, FALSE),
(3, 'Sorts the object', 1, FALSE),
(4, 'Converts the object to a list', 1, FALSE),
(5, 'An immutable list', 2, TRUE),
(6, 'A function', 2, FALSE),
(7, 'A Python module', 2, FALSE),
(8, 'A file format', 2, FALSE),
(9, 'Use a just-in-time compiler like PyPy', 3, TRUE),
(10, 'Remove indentation for faster execution', 3, FALSE),
(11, 'Use more if-else statements', 3, FALSE),
(12, 'Rename variables to shorter names', 3, FALSE),
(13, 'Lists are mutable, tuples are not', 4, TRUE),
(14, 'Tuples have more built-in methods than lists', 4, FALSE),
(15, 'Lists are faster than tuples', 4, FALSE),
(16, 'Tuples can hold more types of objects than lists', 4, FALSE),
(17, 'An anonymous function expressed as a single statement', 5, TRUE),
(18, 'A data type that stores lambda expressions', 5, FALSE),
(19, 'A version control system', 5, FALSE),
(20, 'A loop that iterates over lambda expressions', 5, FALSE),
(21, 'It ensures that resources are properly released after use', 6, TRUE),
(22, 'It imports a module and gives it an alias', 6, FALSE),
(23, 'It creates a new virtual environment', 6, FALSE),
(24, 'It writes data to a file without needing to close the file', 6, FALSE);


-- Software Engineer
INSERT INTO examen_tecnico (id, id_rol_habilidad) VALUES (2, 1);

INSERT INTO examen_pregunta (id, id_examen_tecnico, pregunta, dificultad) VALUES
(7, 2, 'What is the primary function of a software engineer?', 1),
(8, 2, 'Which version control systems are you familiar with?', 1),
(9, 2, 'What is the SOLID principle in software engineering?', 2),
(10, 2, 'How do you ensure that your code is both safe and efficient?', 2),
(11, 2, 'Explain the difference between a mutex and a semaphore.', 3),
(12, 2, 'What is a microservices architecture?', 3);

INSERT INTO examen_respuesta (id, respuesta, id_examen_pregunta, correcta) VALUES
(25, 'To develop software solutions that meet clients needs', 7, TRUE),
(26, 'To fix computers and hardware issues', 7, FALSE),
(27, 'To manage company databases', 7, FALSE),
(28, 'To design computer networks', 7, FALSE),
(29, 'Git, SVN, Mercurial', 8, TRUE),
(30, 'Photoshop, Illustrator', 8, FALSE),
(31, 'Microsoft Word, Excel', 8, FALSE),
(32, 'AutoCAD, SketchUp', 8, FALSE),
(33, 'It is an acronym for five design principles intended to make software designs more understandable, flexible and maintainable.', 9, TRUE),
(34, 'It is a protocol for secure network communications.', 9, FALSE),
(35, 'It is the latest version of the Linux operating system.', 9, FALSE),
(36, 'It is a type of programming language.', 9, FALSE),
(37, 'Through rigorous testing, code reviews, and adherence to best practices', 10, TRUE),
(38, 'By making all variables public', 10, FALSE),
(39, 'Using the fastest algorithm without considering security', 10, FALSE),
(40, 'Coding quickly to meet deadlines', 10, FALSE),
(41, 'A mutex provides mutual exclusion, whereas a semaphore allows more than one thread to access a resource at a time', 11, TRUE),
(42, 'A semaphore is a feature of object-oriented programming, while a mutex is not.', 11, FALSE),
(43, 'A mutex is a type of semaphore specifically for use in web development.', 11, FALSE),
(44, 'Semaphores can only be used in kernel mode, while mutexes can be used in user mode.', 11, FALSE),
(45, 'It is an architectural style that structures an application as a collection of services that are highly maintainable and testable', 12, TRUE),
(46, 'It is a software development method that relies on very frequent releases and updates', 12, FALSE),
(47, 'It is a protocol for wireless network communication', 12, FALSE),
(48, 'It is a programming paradigm based on the concept of "objects"', 12, FALSE);


-- Java
INSERT INTO examen_tecnico (id, id_rol_habilidad) VALUES (3, 38);

INSERT INTO examen_pregunta (id, id_examen_tecnico, pregunta, dificultad) VALUES
(13, 3, 'What is the JVM and why is it important?', 1),
(14, 3, 'Can you explain the concept of thread safety in Java?', 1),
(15, 3, 'What is the difference between Abstract Class and Interface in Java?', 2),
(16, 3, 'How does the Java garbage collector prevent memory leaks?', 2),
(17, 3, 'What are the main principles of object-oriented programming in Java?', 3),
(18, 3, 'How does a HashMap work in Java?', 3);

INSERT INTO examen_respuesta (id, respuesta, id_examen_pregunta, correcta) VALUES
(49, 'JVM stands for Java Virtual Machine, and it is important because it runs Java bytecode on any platform without modification.', 13, TRUE),
(50, 'JVM is a Java version manager used to switch between different Java versions.', 13, FALSE),
(51, 'JVM is a performance monitoring tool exclusively for Java applications.', 13, FALSE),
(52, 'JVM is just another name for the JDK.', 13, FALSE),
(53, 'Thread safety means that a method or class instance can be used by multiple threads at the same time without any problem.', 14, TRUE),
(54, 'Thread safety is a protocol to ensure that threads run in a sequential order.', 14, FALSE),
(55, 'Thread safety is the prevention of threads from creating instances of classes.', 14, FALSE),
(56, 'Thread safety is a feature that allows threads to be paused and resumed.', 14, FALSE),
(57, 'An Abstract Class can have implemented methods, but an Interface can only have method signatures without bodies.', 15, TRUE),
(58, 'Abstract Classes and Interfaces are exactly the same in Java.', 15, FALSE),
(59, 'Interfaces are used to define variables, while Abstract Classes are not.', 15, FALSE),
(60, 'Abstract Classes are deprecated and Interfaces are the modern alternative.', 15, FALSE),
(61, 'It automatically removes objects that are no longer in use and reclaims their memory.', 16, TRUE),
(62, 'It stops the program whenever there''s not enough memory and asks users to free memory.', 16, FALSE),
(63, 'Garbage collection in Java is manually handled by the programmer.', 16, FALSE),
(64, 'Java garbage collector cleans up only the physical hardware part of the memory.', 16, FALSE),
(65, 'Encapsulation, Inheritance, Polymorphism, and Abstraction', 17, TRUE),
(66, 'Compilation, Interpretation, Execution, and Obfuscation', 17, FALSE),
(67, 'Syntax, Semantics, Pragmatics, and Phonology', 17, FALSE),
(68, 'Classes, Objects, Variables, and Arrays', 17, FALSE),
(69, 'It stores elements by inserting the key-value pair into an array-like structure and uses the hash code of the key to determine where to place the pair.', 18, TRUE),
(70, 'It sorts the keys in natural order and associates them with values.', 18, FALSE),
(71, 'HashMap works just like a regular map in Java; the ''HashMap'' is just a fancy name.', 18, FALSE),
(72, 'HashMap duplicates each key multiple times to ensure no data is lost.', 18, FALSE);

-- Javascrist

INSERT INTO examen_tecnico (id, id_rol_habilidad) VALUES (4, 101);

INSERT INTO examen_pregunta (id, id_examen_tecnico, pregunta, dificultad) VALUES
(19, 4, 'Explain the difference between "==" and "===", and when should you use each?', 1),
(20, 4, 'What is a closure in JavaScript and how is it used?', 1),
(21, 4, 'Can you describe the event bubbling process in the DOM?', 2),
(22, 4, 'What is the purpose of the "this" keyword in JavaScript?', 2),
(23, 4, 'How does JavaScript handle asynchronous operations?', 3),
(24, 4, 'What are the advantages of using ES6 modules?', 3);

INSERT INTO examen_respuesta (id, respuesta, id_examen_pregunta, correcta) VALUES
(73, '"==" compares only values, "===" compares both values and types, and "===" should be used when type is also to be considered.', 19, TRUE),
(74, '"==" is used in JavaScript and "===" is used in Java.', 19, FALSE),
(75, '"==" performs a type conversion, "===" does not, and "===" is generally preferred for clarity.', 19, FALSE),
(76, 'There is no difference, it is just a matter of preference.', 19, FALSE),
(77, 'A closure is a function that has access to its own scope, the outer function''s scope, and global scope.', 20, TRUE),
(78, 'A closure is a special object that holds variables after a function has executed.', 20, FALSE),
(79, 'A closure is an error thrown when a function does not finish executing.', 20, FALSE),
(80, 'A closure is a function without a name in JavaScript.', 20, FALSE),
(81, 'Event bubbling is a method of event propagation in the DOM where events bubble up from the deepest, innermost element to its parents.', 21, TRUE),
(82, 'Event bubbling is when an event gets duplicated and the duplicate bubbles up the DOM tree.', 21, FALSE),
(83, 'Event bubbling is a DOM event that specifically relates to CSS animations.', 21, FALSE),
(84, 'Event bubbling is a process where an event starts from the parent and propagates to the child elements.', 21, FALSE),
(85, 'It refers to the object in which the function is called, and it can vary based on the context.', 22, TRUE),
(86, '"this" keyword refers exclusively to the window object.', 22, FALSE),
(87, 'The "this" keyword is a variable that stores the return value of a function.', 22, FALSE),
(88, '"this" keyword is used to declare global variables.', 22, FALSE),
(89, 'JavaScript uses events and callback functions to handle asynchronous operations.', 23, TRUE),
(90, 'JavaScript handles asynchronous operations using multi-threading.', 23, FALSE),
(91, 'Asynchronous operations in JavaScript are not possible; JavaScript can only operate synchronously.', 23, FALSE),
(92, 'Asynchronous operations in JavaScript are handled by sending them to a server and waiting for a response.', 23, FALSE),
(93, 'ES6 modules allow for code to be broken up into smaller, maintainable blocks that can be imported or exported, which enhances code organization and reuse.', 24, TRUE),
(94, 'ES6 modules are faster because they use a different engine under the hood.', 24, FALSE),
(95, 'ES6 modules prevent any code from running and are used for security.', 24, FALSE),
(96, 'ES6 modules are mainly used for providing backward compatibility with older JavaScript versions.', 24, FALSE);


-- HTML/CSS

-- Insert into examen_tecnico table
INSERT INTO examen_tecnico (id, id_rol_habilidad) VALUES (5, 107);

-- Insert into examen_pregunta table
INSERT INTO examen_pregunta (id, id_examen_tecnico, pregunta, dificultad) VALUES
(25, 5, 'What does the "DOCTYPE" declaration do in HTML?', 1),
(26, 5, 'What is the difference between "class" and "id" attributes in HTML?', 1),
(27, 5, 'Describe the "box model" in CSS and its components.', 2),
(28, 5, 'What is the purpose of the "z-index" in CSS?', 2),
(29, 5, 'How would you make a website responsive without using a framework?', 3),
(30, 5, 'Explain the concept of CSS specificity and how it determines which styles are applied.', 3);

-- Insert into examen_respuesta table
INSERT INTO examen_respuesta (id, respuesta, id_examen_pregunta, correcta) VALUES
(97, 'It tells the browser which version of HTML the page is written in.', 25, TRUE),
(98, 'It resets the browser to default settings.', 25, FALSE),
(99, 'It is a declaration that defines the language of the script used in the HTML page.', 25, FALSE),
(100, 'It provides the title of the document.', 25, FALSE),
(101, 'The "class" attribute is used to style multiple elements, while "id" is used for a single unique element.', 26, TRUE),
(102, '"class" is a CSS selector and "id" is an HTML element.', 26, FALSE),
(103, 'There is no difference, they can be used interchangeably.', 26, FALSE),
(104, '"id" is used once per page while "class" can be used multiple times for different tags.', 26, FALSE),
(105, 'It consists of the content, padding, border, and margin of an element.', 27, TRUE),
(106, 'The box model is a layout design for the letters in a paragraph.', 27, FALSE),
(107, 'It refers to the 3D representation of a webpage.', 27, FALSE),
(108, 'It is a JavaScript model for HTML elements.', 27, FALSE),
(109, 'It determines the stacking order of elements that overlap.', 28, TRUE),
(110, 'It defines the zoom level of an element.', 28, FALSE),
(111, 'It sets the transparency level of an element.', 28, FALSE),
(112, 'It''s a function that generates random numbers to order elements.', 28, FALSE),
(113, 'Use media queries in CSS to adjust the layout based on the screen size and resolution.', 29, TRUE),
(114, 'It''s not possible without a framework; frameworks are necessary for responsive design.', 29, FALSE),
(115, 'You would need to manually adjust the screen size using JavaScript.', 29, FALSE),
(116, 'Remove all CSS, as default HTML is already responsive.', 29, FALSE),
(117, 'Specificity is a scoring system that browsers use to determine which CSS rule applies if multiple rules have different selectors.', 30, TRUE),
(118, 'Specificity is determined by the number of elements in the HTML.', 30, FALSE),
(119, 'Specificity is a measure of how many pixels are in an element.', 30, FALSE),
(120, 'CSS specificity is decided by the order of the CSS rules in the stylesheet.', 30, FALSE);


INSERT INTO examen_tecnico (id, id_rol_habilidad) VALUES (6, 109);

INSERT INTO examen_pregunta (id, id_examen_tecnico, pregunta, dificultad) VALUES
(31, 6, 'What is the core concept of Angular.js?', 1),
(32, 6, 'How do you share data between controllers in Angular.js?', 1),
(33, 6, 'Explain what a directive is in Angular.js context.', 2),
(34, 6, 'What are services in Angular.js and when would you use one?', 2),
(35, 6, 'How would you optimize an Angular.js application for better performance?', 3),
(36, 6, 'What is the purpose of the $scope object in Angular.js?', 3);

INSERT INTO examen_respuesta (id, respuesta, id_examen_pregunta, correcta) VALUES
(121, 'To extend HTML with new attributes called Directives.', 31, TRUE),
(122, 'Angular.js is primarily focused on server-side operations.', 31, FALSE),
(123, 'The core concept is to make JavaScript code more strict and less flexible.', 31, FALSE),
(124, 'It introduces a new way to style web components.', 31, FALSE),
(125, 'Using services or the $rootScope to store and retrieve data.', 32, TRUE),
(126, 'Data between controllers cannot be shared in Angular.js.', 32, FALSE),
(127, 'It is done via global variables that are accessible in all controllers.', 32, FALSE),
(128, 'Through direct controller-to-controller communication.', 32, FALSE),
(129, 'Directives are markers on DOM elements that tell Angular.js to attach a specified behavior.', 33, TRUE),
(130, 'A directive is a type of expression used exclusively in Angular.js templates.', 33, FALSE),
(131, 'It is a pre-defined script that executes when the application starts.', 33, FALSE),
(132, 'Directives are Angular.js functions that are called when the page loads.', 33, FALSE),
(133, 'Services are singleton objects that provide functionality for other components of an Angular.js app.', 34, TRUE),
(134, 'Services in Angular.js are used to enable routing across the application.', 34, FALSE),
(135, 'They are decorative functions that add visual elements to the app.', 34, FALSE),
(136, 'Services are plugins for extending Angular.js.', 34, FALSE),
(137, 'Minimize watchers, use one-time binding where possible, and leverage lazy loading.', 35, TRUE),
(138, 'To optimize, remove Angular.js and replace it with pure JavaScript for all operations.', 35, FALSE),
(139, 'Optimization can be done by compressing the Angular.js files.', 35, FALSE),
(140, 'By writing all your code in a single Angular.js controller.', 35, FALSE),
(141, '$scope is a built-in object that holds application data and methods.', 36, TRUE),
(142, 'The $scope object is used to secure the application against unauthorized access.', 36, FALSE),
(143, 'It is a controller component that enhances performance.', 36, FALSE),
(144, '$scope is an Angular.js service provider for dependency injection.', 36, FALSE);

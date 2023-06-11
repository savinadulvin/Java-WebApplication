/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// Handle messages received from the main thread
self.onmessage = function(event) {
    var formData = event.data;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "transaction", true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            // Send the response back to the main thread
            self.postMessage(xhr.responseText);
        }
    };
    xhr.send(formData);
};
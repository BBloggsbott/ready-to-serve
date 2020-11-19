# ready-to-serv

A backend server for people who don't want to write backend code.

Ready to Serve lets you set up a server with functionalities that you want by using plugins to offer those functionalities and map them to endpoints.

## Set up
Download and run ready-to-serve.jar file and it will create the necessary files and directories for you during the first run. You can then customize where you keep the necessary data by editing the `settings.json` file. 

## What are plugins?

Plugins are Jar files. You use a plugin config file to configure aspects of the plugin and mapp it to an endpoint you want. The config file uses a `.yml` syntax.

Here is a sample plugin config:
```yaml
- name: Sum
  jarfile: helloworld.jar
  method: com.bbloggsbott.helloworld.HelloWorld.getSum
  endpoint: /sum
  requestType: GET
  args:
    - name: a
      type: int
      requestParam: true
    - name: b
      type: int
      requestParam: true
- name: Hello Name
  jarfile: helloworld.jar
  method: com.bbloggsbott.helloworld.HelloWorld.getHelloName
  endpoint: /helloname
  requestType: POST
  args:
    - name: name
      type: java.lang.String
      requestParam: false
```  
The Jar files and the config file must be inside the `plugins_directory`. You can find plugins in the plugin library [here](https://github.com/BBloggsbott/ready-to-serv-plugins).

### Plugin config arguments
The plugin config file is a yaml file with a list of objects that provide the following information
* `name` - Name of the Plugin
* `jarfile` - The Jar file that has the method you want to use
* `method` - The method in the jar file you want to use
* `endpoint` - The endpoint you want to map the method to
* `requestType` - The Http Request Method for the request
* `args` - The arguments needed for the method

#### Defining arguments for mapping endpoint to method
 The `args` parameter is a list of objects that define the objects needed to make the method call. These parameters must be listed in the same order as the method signature in the Jar file. The following parameters are necessary to define an argument.
 * `name` - Name of the argument. This name will be used to fetch the parameter from the request for processing.
 * `type` - This defines the type of the argument
 * `requestParam` - This is used to determine if the argument will be part of the request param (`true`) or the request body (`false`).
 
 **Note**: Ready to Serve currently cannot load argument types from the Jar file. This enhancement will be added in future releases. 
 
 ## What else can Ready to Serve do?
 ### Serving static content
 Ready to serve can serve text as html and markdown using as Pages. This can be done by creating pages in the `pages_directory`. The syntax for a page is show below
```markdown
---
pagemeta
title: This is the page title
url_path: /somepath/demopath
date: 13-10-2020
excerpt: This is an excerpt for this page
---
# Sample page

This is a sample page. The content in this page is treated as markdown. The server will only load files with `.md` extension.
```
#### Page meta for Pages
Every file for a Page begins with a `pagemeta` block. This can be ignored and default values will be used. The server will live-reload content when creating/updating/deleting pages, so you don't have to restart the server every time you need to make a change.

##### Page Meta parameters
* `title` - Title of the Page (defaults to file name)
* `url_path` - Endpoint the page gets mapped to (defaults to path from `pages_directory`)
* `date` - Date of creation of page (defaults to date of loading the page)
* `excerpt` - Excerpt of the content of the page (defaults to first 128 characters of the page content)

Editing any content of the page will trigger a live reload for that page alone. Live reload can also update endpoints of the page.

### Serving Files
Ready to serve can be used to serve files by having them in the `files_directory`. They can be fetched using the endpoint `/files/<name_of_file>`.

**Note**: Ready to serve cannot serve huge files as streaming has not been implemented yet.

## Default endpoints
* `/` - This endpoint returns some information about the server
* `/routes` - This endpoint returns the first step in the path to pages. For example, if you have pages mapped to `/somepagepath/somepage`, `/somepagedir/somepage2`, `/somepagedir/somepagepath2/apage`, a GET request to `/routes` will return [`/somepagepath`, `/somepagedir`]. You can then hit either of these to get the next step in the path. The responses will have an `is_page` parameter to show if the path is an endpoint mapped to a page or is a step in the path to a page.

## Running the application using Maven
To run the application using maven,
Clone the repository
```
$ git clone https://github.com/BBloggsbott/ready-to-serv.git
$ cd ready-to-serve
```

Run 
```
$ mvn spring-boot:run
```
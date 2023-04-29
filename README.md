# Integration: Maps

Cedric Sirianni, Jessica Ni

## Purpose 
Use MapBox API and public redlining data to visualize history of inequality throughout US cities. API server supports bounding box query to filter geographical data.

## Design Choices

We organized our project into two sections: front-end and back-end. The front-end section includes our React code used to produce the map visible to user. Our back-end section provides an API for filtering GeoJSON data according to a bounding box. 

### Front-end

To produce the map, we created an `App` component that renders several nested components: `Map`, `Source,` and `Layer`. This involved using states for the `viewState` and `overlay`. 

The data for redlining was fetched from the back-end code. We used a `useEffect` hook to render this data one time only. The `overlay` state is modified in the lambda expression defined within the `useEffect` hook.

### Back-end

To provide the data to the front-end, we created a `filter` endpoint using the `FilterHander`. The back-end stores the full GeoJSON data. We then filter the data according to the minimum and maximum latitude and longitude specified in the GET request. This computation is $\theta(n)$ for a list of `Feature`s length $n$. That is quite expensive, and sorting could potentially improve runtime significantly. 

## Testing

Testing for this project can be found in back-end/src/tests. We tested the following properties of our program:

- endpoint creation
- filter endpoint with no args
- filter endpoint with four args
  - args exclude all `Feature`s
  - args include some `Feature`s
  - args include all `Feature`s
- mock GeoJSON data
  - empty `Feature` list
  - non-empty `Feature` list
- filtration given some arbitrary args via fuzzing

## Instructions

In order to run the project, start the back-end server to create an endpoint at http://localhost:3232/

Run `npm start` in the front-end directory in order to start website.

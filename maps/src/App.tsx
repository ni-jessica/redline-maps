import React, { useState } from 'react';
import Map from "react-map-gl";
import 'mapbox-gl/dist/mapbox-gl.css';
import { mapboxToken } from './private/variables';
import './App.css';

type ProvidenceLatLong = {
  lat: number,
  long: number
}

function App() {

  const coordinates: ProvidenceLatLong = {
    lat: 41.8240,
    long: 71.4128
  }

  const [viewState, setViewState] = React.useState({
    latitude: coordinates.lat,
    longitude: coordinates.long,
    zoom: 5
  })

  return (
    <div>
      <Map mapboxAccessToken={mapboxToken}
        longitude={viewState.longitude}
        latitude={viewState.latitude}
        zoom={viewState.zoom} />
    </div>
  );
}

export default App;

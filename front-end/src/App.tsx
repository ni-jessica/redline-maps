import { useEffect, useState } from 'react';
import Map, { Source, Layer, ViewStateChangeEvent, MapLayerMouseEvent } from "react-map-gl";
import { overlayData, geoLayer } from './overlay';
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
    long: -71.4128
  }

  const [viewState, setViewState] = useState({
    latitude: coordinates.lat,
    longitude: coordinates.long,
    zoom: 5
  })

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined);

  // Run this once, and never refresh (because of the empty dependency list)
  useEffect(() => {
    setOverlay(overlayData);
  }, []);

  function onMapClick(e: MapLayerMouseEvent) {
    console.log(e.lngLat.lat);
    console.log(e.lngLat.lng);
  }

  return (
      <Map mapboxAccessToken={mapboxToken}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom} 
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        onClick={onMapClick}
        style={{width: window.innerWidth, height: window.innerHeight}}
        mapStyle={'mapbox://styles/mapbox/streets-v11'}>
          <Source id="geo_data" type="geojson" data={overlay}>
            <Layer {...geoLayer} />
          </Source>
      </Map>
  );
}

export default App;

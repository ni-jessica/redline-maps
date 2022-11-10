import { FillLayer } from "react-map-gl";
import { FeatureCollection } from "geojson";

// import the raw JSON file
// import rl_data from "./mockData/fullDownload.json";

export async function getFilteredData(latMin: number, latMax: number, lonMin: number, lonMax:number): Promise<JSON> {
    const response: Response = await fetch(`http://localhost:3232/filter?latMin=${latMin}&latMax=${latMax}&lonMin=${lonMin}&lonMax=${lonMax}`)
    return await response.json();
}
// Type predicate for FeatureCollection
export function isFeatureCollection(json: any): json is FeatureCollection {
    return json.type === "FeatureCollection";
}

const propertyName = 'holc_grade';

export const geoLayer: FillLayer = {
    id: 'geo_data',
    type: 'fill',
    paint: {
        'fill-color': [
            'match', 
            ['get', propertyName],
            'A',
            '#5bcc04',
            'B',
            '#04b8cc',
            'C',
            '#e9ed0e',
            'D',
            '#d11d1d',
            '#ccc'
        ],
        'fill-opacity': 0.2
    }
};
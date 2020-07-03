/*
 * Copyright (C) 2017 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.picam.server;

import com.openitvn.picam.proc.BoardPattern;
import com.openitvn.picam.proc.LensCalibrator;
import com.openitvn.picam.proc.BlobDetector;
import com.openitvn.picam.proc.BlobFilter;
import com.openitvn.picam.proc.ParamEntry;
import com.openitvn.picam.proc.LensEntry;
import com.openitvn.picam.Parameter;
import com.openitvn.picam.PicamVideo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Thinh Pham
 */
public abstract class PicamProfile {
    
    public static boolean save(PicamVideo vid, LensCalibrator calib, BlobDetector detect, OutputStream out) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootNode = (Element) doc.createElement("profile");
            rootNode.setAttribute("width", Integer.toString(vid.getWidth()));
            rootNode.setAttribute("height", Integer.toString(vid.getHeight()));
            rootNode.setAttribute("mode", Integer.toString(vid.getSensorMode()));
            doc.appendChild(rootNode);
            // calib (multiple)
            for (int i = 0; i < calib.getRowCount(); i++) {
                LensEntry e = calib.getEntry(i);
                // calib
                Element calibNode = doc.createElement("calib");
                if (e.active)
                    calibNode.setAttribute("active", "true");
                rootNode.appendChild(calibNode);
                // calib >> title
                if (e.title != null)
                    calibNode.appendChild(doc.createElement("title"))
                            .appendChild(doc.createTextNode(e.title));
                // calib >> time
                calibNode.appendChild(doc.createElement("time"))
                        .appendChild(doc.createTextNode(Long.toString(e.time)));
                // calib >> board_type
                calibNode.appendChild(doc.createElement("type"))
                        .appendChild(doc.createTextNode(e.pattern.code));
                // calib >> board_size
                calibNode.appendChild(doc.createElement("size"))
                        .appendChild(doc.createTextNode(e.pattern.width + " " + e.pattern.height));
                // calib >> flags
                calibNode.appendChild(doc.createElement("flags"))
                        .appendChild(doc.createTextNode(Integer.toString(calib.getFlags())));
                // calib >> camera_intrinsic
                calibNode.appendChild(doc.createElement("intrinsic"))
                        .appendChild(doc.createTextNode(toString(e.intrinsic)));
                // calib >> distortion_coefficients
                calibNode.appendChild(doc.createElement("distortion"))
                        .appendChild(doc.createTextNode(toString(e.distCoeffs)));
                // calib >> reprojection_error
                calibNode.appendChild(doc.createElement("error"))
                        .appendChild(doc.createTextNode(Double.toString(e.error)));
            }
            // blob detectors
            for (int i = 0; i < detect.getRowCount(); i++) {
                ParamEntry e = detect.getEntry(i);
                // detect
                Element detectNode = doc.createElement("blob");
                rootNode.appendChild(detectNode);
                if (e.active)
                    detectNode.setAttribute("active", "true");
                // detect >> title
                if (e.title != null)
                    detectNode.appendChild(doc.createElement("title"))
                            .appendChild(doc.createTextNode(e.title));
                // detect >> iso
                detectNode.appendChild(doc.createElement("iso"))
                        .appendChild(doc.createTextNode(Integer.toString(e.iso)));
                // detect >> imxfx
                String cnt = String.format("%1$d<!-- %2$s -->", e.imxfx.id, e.imxfx);
                detectNode.appendChild(doc.createElement("imxfx"))
                        .appendChild(doc.createTextNode(cnt));
                // video >> sharpness
                detectNode.appendChild(doc.createElement("sharpness"))
                        .appendChild(doc.createTextNode(Integer.toString(e.sharpness)));
                // detect >> contrast
                detectNode.appendChild(doc.createElement("contrast"))
                        .appendChild(doc.createTextNode(Integer.toString(e.contrast)));
                // detect >> brightness
                detectNode.appendChild(doc.createElement("brightness"))
                        .appendChild(doc.createTextNode(Integer.toString(e.brightness)));
                // detect >> filter (multiple)
                for (BlobFilter f : e.filters) {
                    Element node = (Element) detectNode.appendChild(doc.createElement("filter"));
                    node.setAttribute("name", f.type.code);
                    node.setAttribute("active", Boolean.toString(f.active));
                    node.appendChild(doc.createTextNode(Float.toString(f.value)));
                }
            }
            // write the content into xml file
            DOMSource src = new DOMSource(doc);
            StreamResult dst = new StreamResult(out);
            TransformerFactory.newInstance().newTransformer().transform(src, dst);
            return true;
        } catch (ParserConfigurationException | TransformerException ex) {
            return false;
        }
    }

    private static String toString(double[] data) {
        StringBuilder sb = new StringBuilder();
        for (double val : data)
            sb.append(val).append(" ");
        return sb.toString().trim();
    }

    public static boolean load(PicamVideo vid, LensCalibrator calib, BlobDetector blob, InputStream is) throws IOException {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();
            Element curNode = (Element) doc.getElementsByTagName("profile").item(0);
            int width = Integer.parseInt(curNode.getAttribute("width"));
            int height = Integer.parseInt(curNode.getAttribute("height"));
            vid.setResolution(width, height);
            vid.setSensorMode(Integer.parseInt(curNode.getAttribute("mode")));
            NodeList curList;
            Node tmpNode;
            // calib (multiple)
            curList = doc.getElementsByTagName("calib");
            boolean active = false;
            for (int i = 0; i < curList.getLength(); i++) {
                LensEntry e = new LensEntry();
                calib.addEntry(e);
                curNode = (Element) curList.item(i);
                // calib >> title
                if ((tmpNode = curNode.getElementsByTagName("title").item(0)) != null)
                    e.title = tmpNode.getTextContent();
                // calib >> time
                if ((tmpNode = curNode.getElementsByTagName("time").item(0)) != null)
                    e.time = Long.parseLong(tmpNode.getTextContent());
                // calib >> board_type
                if ((tmpNode = curNode.getElementsByTagName("type").item(0)) != null) {
                    e.pattern = BoardPattern.fromCode(tmpNode.getTextContent());
                    // calib >> board_size
                    if ((tmpNode = curNode.getElementsByTagName("size").item(0)) != null) {
                        String[] board_size = tmpNode.getTextContent().split("\\s+");
                        e.pattern.width = Integer.parseInt(board_size[0]);
                        e.pattern.height = Integer.parseInt(board_size[1]);
                    }
                }
                // calib >> flags
                if ((tmpNode = curNode.getElementsByTagName("flags").item(0)) != null)
                    e.flags = Integer.parseInt(tmpNode.getTextContent());
                // calib >> camera_intrinsic
                if ((tmpNode = curNode.getElementsByTagName("intrinsic").item(0)) != null) {
                    String[] camera_intrinsic = tmpNode.getTextContent().split("\\s+");
                    for (int j = 0; j < 9; j++)
                        e.intrinsic[j] = Double.parseDouble(camera_intrinsic[j]);
                }
                // calib >> distortion_coefficients
                if ((tmpNode = curNode.getElementsByTagName("distortion").item(0)) != null) {
                    String[] distortion_coefficients = tmpNode.getTextContent().split("\\s+");
                    for (int j = 0; j < 5; j++)
                        e.distCoeffs[j] = Double.parseDouble(distortion_coefficients[j]);
                }
                // calib >> reprojection_error
                if ((tmpNode = curNode.getElementsByTagName("error").item(0)) != null)
                    e.error = Double.parseDouble(tmpNode.getTextContent());
                // active first node which has active attribute,
                // or the last one if there are no node is actived
                if (!active && (i == curList.getLength() - 1 ||
                        Boolean.parseBoolean(curNode.getAttribute("active")))) {
                    vid.setCalibration(e.intrinsic, e.distCoeffs);
                    calib.activeEntry(i);
                    active = true;
                }
            }
            // blob detectors
            active = false;
            curList = doc.getElementsByTagName("blob");
            for (int i = 0; i < curList.getLength(); i++) {
                ParamEntry e = new ParamEntry();
                blob.addEntry(e);
                curNode = (Element) curList.item(i);
                // detect >> title
                if ((tmpNode = curNode.getElementsByTagName("title").item(0)) != null)
                    e.title = tmpNode.getTextContent();
                // detect >> imxfx
                if ((tmpNode = curNode.getElementsByTagName("imxfx").item(0)) != null)
                    e.imxfx = Parameter.getImageFx(Integer.parseInt(tmpNode.getTextContent()));
                // detect >> iso
                if ((tmpNode = curNode.getElementsByTagName("iso").item(0)) != null)
                    e.iso = Integer.parseInt(tmpNode.getTextContent());
                // detect >> sharpness
                if ((tmpNode = curNode.getElementsByTagName("sharpness").item(0)) != null)
                    e.sharpness = Integer.parseInt(tmpNode.getTextContent());
                // detect >> contrast
                if ((tmpNode = curNode.getElementsByTagName("contrast").item(0)) != null)
                    e.contrast = Integer.parseInt(tmpNode.getTextContent());
                // detect >> brightness
                if ((tmpNode = curNode.getElementsByTagName("brightness").item(0)) != null)
                    e.brightness = Integer.parseInt(tmpNode.getTextContent());
                // detect >> filter (multiple)
                NodeList filterList = curNode.getElementsByTagName("filter");
                for (int j = 0; j < filterList.getLength(); j++) {
                    Element filterNode = (Element) filterList.item(j);
                    BlobFilter filter = e.getFilter(filterNode.getAttribute("name").trim());
                    filter.active = Boolean.parseBoolean(filterNode.getAttribute("active"));
                    filter.value = Float.parseFloat(filterNode.getTextContent());
                }
                // active first node which has active attribute,
                // or the last one if there are no node is actived
                if (!active && (i == curList.getLength() - 1 ||
                        Boolean.parseBoolean(curNode.getAttribute("active")))) {
                    blob.activeEntry(i);
                    e.applyTo(vid);
                    active = true;
                }
            }
            return true;
        } catch (ParserConfigurationException | SAXException ex) {
            return false;
        }
    }
}
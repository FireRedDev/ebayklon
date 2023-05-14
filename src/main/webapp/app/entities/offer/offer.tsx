import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IOffer } from 'app/shared/model/offer.model';
import { getEntities } from './offer.reducer';

export const Offer = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const offerList = useAppSelector(state => state.offer.entities);
  const loading = useAppSelector(state => state.offer.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="offer-heading" data-cy="OfferHeading">
        Offers
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Liste aktualisieren
          </Button>
          <Link to="/offer/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Offer erstellen
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {offerList && offerList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Offer Value</th>
                <th>Auction of Offer</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {offerList.map((offer, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/offer/${offer.id}`} color="link" size="sm">
                      {offer.id}
                    </Button>
                  </td>
                  <td>{offer.offerValue}</td>
                  <td>{offer.offerName ? <Link to={`/auction/${offer.offerName.id}`}>{offer.offerName.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/offer/${offer.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Details</span>
                      </Button>
                      <Button tag={Link} to={`/offer/${offer.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Bearbeiten</span>
                      </Button>
                      <Button tag={Link} to={`/offer/${offer.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Löschen</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">Keine Offers gefunden</div>
        )}
      </div>
    </div>
  );
};

export default Offer;

import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './auction.reducer';

export const AuctionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const auctionEntity = useAppSelector(state => state.auction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auctionDetailsHeading">Auction</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{auctionEntity.id}</dd>
          <dt>
            <span id="auctionDescription">Auction Description</span>
          </dt>
          <dd>{auctionEntity.auctionDescription}</dd>
          <dt>
            <span id="auctionDescription">Offers</span>
          </dt>
          {auctionEntity.auctionNames && auctionEntity.auctionNames.length > 0 ? (
            <dd>
              {auctionEntity.auctionNames.map((offer, i) => (
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
                    </div>
                  </td>
                </tr>
              ))}
            </dd>
          ) : (
            <div className="alert alert-warning">Keine Offers gefunden</div>
          )}
        </dl>
        <Button tag={Link} to="/auction" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Zurück</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/auction/${auctionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Bearbeiten</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuctionDetail;
